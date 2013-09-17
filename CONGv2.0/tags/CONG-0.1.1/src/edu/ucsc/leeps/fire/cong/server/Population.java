package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.ClientInterface;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.logging.MessageEvent;
import edu.ucsc.leeps.fire.cong.logging.TickEvent;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author jpettit
 */
public class Population implements Serializable {

    private Map<Integer, ClientInterface> members;
    private long periodStartTime;
    private Set<Tuple> groups;
    private Map<Integer, Tuple> groupMap;
    private Map<Integer, Float> subperiodPayoffs;
    private TickEvent tick = new TickEvent();
    private Map<Integer, BlockingQueue<StrategyUpdateEvent>> strategyUpdateEvents;
    private Map<Integer, StrategyUpdateProcessor> strategyUpdateProcessors;
    private MessageEvent mEvent = new MessageEvent();
    private final Object logLock = new Object();

    public Population() {
        groups = new HashSet<Tuple>();
        groupMap = new HashMap<Integer, Tuple>();
        subperiodPayoffs = new HashMap<Integer, Float>();
        strategyUpdateEvents = new HashMap<Integer, BlockingQueue<StrategyUpdateEvent>>();
        strategyUpdateProcessors = new HashMap<Integer, StrategyUpdateProcessor>();
    }

    public void configure(Map<Integer, ClientInterface> members, Map<Integer, String> aliases, Map<Integer, Color> colors) {
        this.members = members;
        for (int member : members.keySet()) {
            strategyUpdateEvents.put(member, new LinkedBlockingQueue<StrategyUpdateEvent>());
            strategyUpdateProcessors.put(member, new StrategyUpdateProcessor(strategyUpdateEvents.get(member)));
            strategyUpdateProcessors.get(member).start();
        }
        FIRE.server.getConfig().payoffFunction.configure();
        if (FIRE.server.getConfig().counterpartPayoffFunction != null ) {
            FIRE.server.getConfig().counterpartPayoffFunction.configure();
        }
        // fixme: better way of doing this. config can auto-configure some parts?
        if (FIRE.server.getConfig().payoffFunction.getNumStrategies() <= 2
                && FIRE.server.getConfig().counterpartPayoffFunction != null
                && FIRE.server.getConfig().payoffFunction instanceof TwoStrategyPayoffFunction) {
            ((TwoStrategyPayoffFunction) FIRE.server.getConfig().counterpartPayoffFunction).isCounterpart = true;
        }
        setupGroups();
        if (FIRE.server.getConfig().preLength == 0) {
            setInitialStrategies();
        }
        if (FIRE.server.getConfig().turnTaking) {
            setInitiative();
        }
        for (Tuple group : groups) {
            List<String> possible_aliases = new ArrayList<String>();
            for (int i = 0; i < group.members.size(); i++) {
                possible_aliases.add(Config.aliases[i]);
            }
            Collections.shuffle(possible_aliases, FIRE.server.getRandom());
            int i = 0;
            for (int id : group.members) {
                aliases.put(id, possible_aliases.get(i));
                for (int j = 0; j < Config.aliases.length; j++) {
                    if (aliases.get(id).equals(Config.aliases[j])) {
                        colors.put(id, Config.colors[j]);
                        break;
                    }
                }
                i++;
            }
        }
        for (int id : members.keySet()) {
            FIRE.server.getConfig(id).currAliases = aliases;
            FIRE.server.getConfig(id).currColors = colors;
            FIRE.server.getConfig(id).payoffFunction.configure();
            FIRE.server.getConfig(id).counterpartPayoffFunction.configure();
        }
    }

    public void setPeriodStartTime() {
        periodStartTime = System.nanoTime();
        for (Tuple group : groups) {
            group.evalTime = periodStartTime;
            group.update(-1);
        }
    }

    public void strategyChanged(int whoChanged, float[] newStrategy, float[] targetStrategy) {
        groupMap.get(whoChanged).update(whoChanged, newStrategy, targetStrategy);
    }

    public void evaluate() {
        for (Tuple group : groups) {
            group.evaluate();
        }
    }

    public void endSubperiod(int subperiod) {
        if (FIRE.server.getConfig().subperiodRematch) {
            shuffleGroups();
        }
        if (FIRE.server.getConfig().probPayoffs) {
            for (Tuple tuple : groups) {
                tuple.realizeStrategies();
            }
        }
        for (Tuple tuple : groups) {
            tuple.evaluateSubperiod(subperiod);
        }
        for (Tuple tuple : groups) {
            tuple.endSubperiod(subperiod);
        }
    }

    public void endPeriod() {
        for (Tuple tuple : groups) {
            tuple.endPeriod();
        }
    }

    public void logTick(int subperiod, int secondsLeft) {
        synchronized (logLock) {
            // Log the tick information
            String period = FIRE.server.getConfig().period;
            float length = FIRE.server.getConfig().length;
            float percent = (float) (length * secondsLeft) / (float) length;
            for (int member : members.keySet()) {
                tick.period = period;
                tick.subject = member;
                tick.config = FIRE.server.getConfig(member);
                tick.subperiod = subperiod;
                tick.secondsLeft = secondsLeft;
                Tuple tuple = groupMap.get(member);
                tick.population = tuple.population;
                tick.world = tuple.world;
                tick.strategy = tuple.strategies.get(member);
                tick.target = tuple.targets.get(member);
                tick.match = tuple.match.population;
                if (tick.config.subperiods != 0 && tick.config.probPayoffs) {
                    tick.payoff = tick.config.payoffFunction.getPayoff(
                            member, percent,
                            tuple.realizedStrategies, tuple.match.realizedStrategies,
                            tick.config);
                    tick.realizedStrategy = tuple.realizedStrategies.get(member);
                    tick.realizedPopStrategy = tick.config.payoffFunction.getPopStrategySummary(member, percent, tuple.realizedStrategies, tuple.match.realizedStrategies);
                    tick.realizedMatchStrategy = tick.config.payoffFunction.getMatchStrategySummary(member, percent, tuple.realizedStrategies, tuple.match.realizedStrategies);
                } else {
                    tick.payoff = tick.config.payoffFunction.getPayoff(
                            member, percent,
                            tuple.strategies, tuple.match.strategies,
                            tick.config);
                }
                // get summary statistics from payoff function
                tick.popStrategy = tick.config.payoffFunction.getPopStrategySummary(member, percent, tuple.strategies, tuple.match.strategies);
                tick.matchStrategy = tick.config.payoffFunction.getMatchStrategySummary(member, percent, tuple.strategies, tuple.match.strategies);
                FIRE.server.commit(tick);
            }
        }
    }

    private class Tuple {

        public int population;
        public int world;
        public boolean discovered;
        public int pathDist;
        public Set<Integer> members;
        public long evalTime;
        public Map<Integer, float[]> strategies;
        public Map<Integer, float[]> targets;
        public Map<Integer, float[]> realizedStrategies;
        public Tuple match;

        public Tuple() {
            this(groups.size());
        }

        public Tuple(int population) {
            this.population = population;
            groups.add(this);
            members = new HashSet<Integer>();
            strategies = new HashMap<Integer, float[]>();
            targets = new HashMap<Integer, float[]>();
            realizedStrategies = new HashMap<Integer, float[]>();
        }

        public void update(int whoChanged, float[] strategy, float[] target) {
            if (FIRE.server.getConfig().subperiods == 0) {
                evaluate();
                if (this != match) {
                    match.evaluate();
                }
            }
            strategies.put(whoChanged, strategy);
            targets.put(whoChanged, target);
            if (FIRE.server.getConfig().subperiods == 0) {
                update(whoChanged);
            }
        }

        public void update(int whoChanged) {
            for (int member : members) {
                strategyUpdateEvents.get(member).add(
                        new StrategyUpdateEvent(member, whoChanged, strategies, null));
            }
            for (int member : match.members) {
                strategyUpdateEvents.get(member).add(
                        new StrategyUpdateEvent(member, whoChanged, null, strategies));
            }
        }

        public void evaluate() {
            long timestamp = System.nanoTime();
            float percent = (timestamp - periodStartTime) / (FIRE.server.getConfig().length * 1000000000f);
            float percentElapsed = (timestamp - evalTime) / (FIRE.server.getConfig().length * 1000000000f);
            if (percentElapsed > 0.01) {
                evaluate(percent, percentElapsed);
                evalTime = timestamp;
            }
        }

        public void evaluate(float percent, float percentElapsed) {
            for (int member : members) {
                Config config = FIRE.server.getConfig(member);
                PayoffFunction u = config.payoffFunction;
                float payoff;
                if (config.probPayoffs) {
                    payoff = u.getPayoff(
                            member, percent,
                            realizedStrategies, match.realizedStrategies,
                            config);
                } else {
                    payoff = u.getPayoff(
                            member, percent,
                            strategies, match.strategies,
                            config);
                }
                subperiodPayoffs.put(member, payoff);
                if (config.indefiniteEnd == null) {
                    payoff *= percentElapsed;
                }
                FIRE.server.addToPeriodPoints(member, payoff);
            }
        }

        public void realizeStrategies() {
            realizedStrategies.clear();
            for (int member : members) {
                float[] s = strategies.get(member);
                float[] a = new float[s.length];
                float total = 1;
                for (int i = 0; i < s.length; i++) {
                    if (FIRE.server.getRandom().nextFloat() <= s[i] / total) {
                        a[i] = 1;
                        break;
                    }
                    total -= s[i];
                }
                realizedStrategies.put(member, a);
            }
        }

        public void evaluateSubperiod(final int subperiod) {
            float percentElapsed = 1f / FIRE.server.getConfig().subperiods;
            float percent = subperiod * percentElapsed;
            evaluate(percent, percentElapsed);
        }

        public void endSubperiod(final int subperiod) {
            for (final int member : members) {
                new Thread() {

                    @Override
                    public void run() {
                        Config config = FIRE.server.getConfig(member);
                        FIRE.server.addToPeriodPoints(
                                member,
                                config.payoffFunction.getSubperiodBonus(subperiod, config));
                        float payoff = subperiodPayoffs.get(member);
                        float matchPayoff = 0;
                        for (int matchMember : Tuple.this.match.members) {
                            matchPayoff += subperiodPayoffs.get(matchMember);
                        }
                        matchPayoff /= Tuple.this.match.members.size();
                        Population.this.members.get(member).endSubperiod(
                                subperiod, strategies, match.strategies, payoff, matchPayoff);
                    }
                }.start();
            }
            update(-1);
        }

        public void endPeriod() {
            evaluate();
        }
    }

    public void setupGroups() {
        groups.clear();
        groupMap.clear();
        if (FIRE.server.getConfig().numGroups == 1
                || (members.size() / FIRE.server.getConfig().groupSize) == 1) {
            setupSinglePopGroups();
        } else {
            if (FIRE.server.getConfig().assignedGroups) {
                setupAssignedGroups();
            } else {
                setupRandomGroups();
            }
        }
        //setWorlds();
    }

    /*
     * Constructs a single tuple comprising all subjects
     * Tuple is linked to itself
     */
    private void setupSinglePopGroups() {
        Tuple group = new Tuple();
        group.members = members.keySet();
        group.match = group;
        for (int member : members.keySet()) {
            Config def = FIRE.server.getConfig();
            Config config = FIRE.server.getConfig(member);
            config.isCounterpart = false;
            config.payoffFunction = def.payoffFunction;
            config.counterpartPayoffFunction = def.payoffFunction;
            groupMap.put(member, group);
            config.playersInGroup = members.size();
        }
    }

    private void setupAssignedGroups() {
        Map<Integer, Tuple> populations = new HashMap<Integer, Tuple>();
        for (int member : members.keySet()) {
            Config config = FIRE.server.getConfig(member);
            if (!populations.containsKey(config.population)) {
                populations.put(config.population, new Tuple(config.population));
            }
            if (!populations.containsKey(config.match)) {
                populations.put(config.match, new Tuple(config.match));
            }
            populations.get(config.population).members.add(member);
            populations.get(config.population).match = populations.get(config.match);
            groupMap.put(member, populations.get(config.population));
            groups.add(populations.get(config.population));
            groups.add(populations.get(config.match));
        }
        Set<Tuple> assignedMatches = new HashSet<Tuple>();
        for (Tuple group : groups) {
            if (assignedMatches.contains(group)) {
                continue;
            }
            if (group.population == group.match.population) {
                for (int member : group.members) {
                    Config config = FIRE.server.getConfig(member);
                    config.playersInGroup = group.members.size();
                }
            } else {
                if (group.population > group.match.population) {
                    group = group.match;
                }
                for (int member : group.members) {
                    Config config = FIRE.server.getConfig(member);
                    config.playersInGroup = group.members.size();
                }
                for (int member : group.match.members) {
                    Config config = FIRE.server.getConfig(member);
                    config.playersInGroup = group.match.members.size();
                }
            }
            assignedMatches.add(group);
            assignedMatches.add(group.match);
        }
    }

    private void setupRandomGroups() {
        Config config = FIRE.server.getConfig();
        ArrayList<Integer> randomMembers = new ArrayList<Integer>();
        randomMembers.addAll(members.keySet());
        Collections.shuffle(randomMembers, FIRE.server.getRandom());
        if (config.groupSize == -1) {
            config.groupSize = members.size() / config.numGroups;
        }
        Tuple current = null;
        ArrayList<Tuple> randomGroups = new ArrayList<Tuple>();
        while (randomMembers.size() > 0) {
            if (current == null || current.members.size() == config.groupSize) {
                current = new Tuple();
                randomGroups.add(current);
            }
            int member = randomMembers.remove(0);
            current.members.add(member);
            groupMap.put(member, current);
        }
        if (config.matchType == Config.MatchGroup.pair) {
            Collections.shuffle(randomGroups, FIRE.server.getRandom());
            while (randomGroups.size() > 0) {
                Tuple group = randomGroups.remove(0);
                if (groups.size() == 1) {
                    group.match = group;
                } else {
                    group.match = randomGroups.remove(0);
                }
                group.match.match = group;
                Tuple group1;
                if (FIRE.server.getRandom().nextBoolean()) {
                    group1 = group;
                } else {
                    group1 = group.match;
                }
                PayoffFunction payoffFunction = config.payoffFunction;
                PayoffFunction counterpartPayoffFunction = config.counterpartPayoffFunction == null ? config.payoffFunction : config.counterpartPayoffFunction;
                for (int member : group1.members) {
                    Config c = FIRE.server.getConfig(member);
                    c.isCounterpart = false;
                    c.payoffFunction = payoffFunction;
                    c.counterpartPayoffFunction = counterpartPayoffFunction;
                    c.playersInGroup = group1.members.size();
                }
                for (int member : group1.match.members) {
                    Config c = FIRE.server.getConfig(member);
                    c.isCounterpart = true;
                    c.payoffFunction = counterpartPayoffFunction;
                    c.counterpartPayoffFunction = payoffFunction;
                    c.playersInGroup = group1.match.members.size();
                }
            }
        } else {
            for (Tuple group : groups) {
                group.match = group;
                for (int member : group.members) {
                    Config c = FIRE.server.getConfig(member);
                    c.isCounterpart = false;
                    c.payoffFunction = config.payoffFunction;
                    c.counterpartPayoffFunction = config.payoffFunction;
                    c.playersInGroup = group.members.size();
                }
            }
        }
        assert (randomMembers.isEmpty());
        assert (randomGroups.isEmpty());
    }

    private void setInitialStrategies() {
        for (int client : members.keySet()) {
            Config config = FIRE.server.getConfig(client);
            float[] s;
            if (!Float.isNaN(config.initial)) {
                s = new float[]{config.initial,};
            } else if (!Float.isNaN(config.initial0) && !Float.isNaN(config.initial1)) {
                s = new float[3];
                s[0] = config.initial0;
                s[1] = config.initial1;
                s[2] = 1 - s[1] - s[0];
            } else if (config.payoffFunction.getNumStrategies() <= 2) {
                s = new float[1];
                if (config.mixed) {
                    float costRange = config.payoffFunction.getMax() - config.marginalCost;
                    float totalRange = config.payoffFunction.getMax() - config.payoffFunction.getMin();
                    s[0] = (FIRE.server.getRandom().nextFloat() * costRange + config.marginalCost) / totalRange;
                } else {
                    s[0] = FIRE.server.getRandom().nextBoolean() ? 1 : 0;
                }
            } else if (config.payoffFunction.getNumStrategies() == 3) {
                s = new float[3];
                if (config.mixed) {
                    s[0] = FIRE.server.getRandom().nextFloat();
                    s[1] = (1 - s[0]) * FIRE.server.getRandom().nextFloat();
                    s[2] = 1 - s[0] - s[1];
                } else {
                    s[0] = 0;
                    s[1] = 0;
                    s[2] = 0;
                    s[FIRE.server.getRandom().nextInt(3)] = 1;
                }
            } else {
                System.err.println(config.payoffFunction);
                throw new IllegalStateException("Cannot set initial strategies for given payoff function");
            }
            config.initialStrategy = s;
        }
        for (Tuple group : groups) {
            for (int member : group.members) {
                group.strategies.put(member, FIRE.server.getConfig(member).initialStrategy);
                group.targets.put(member, FIRE.server.getConfig(member).initialStrategy);
            }
        }
        for (Tuple group : groups) {
            group.update(-1);
        }
    }

    private void setInitiative() {
        for (Tuple group : groups) {
            List<Integer> l = new ArrayList<Integer>();
            for (Integer member : group.members) {
                l.add(member);
            }
            Collections.shuffle(l);
            int[] initiatives = new int[group.members.size()];
            int i = 0;
            for (Integer m : l) {
                initiatives[i++] = m;
            }
            for (Integer member : group.members) {
                FIRE.server.getConfig(member).initiatives = initiatives;
            }
        }
    }

    private void shuffleGroups() {
        ArrayList<Tuple> randomGroups = new ArrayList<Tuple>();
        for (Tuple tuple : groups) {
            randomGroups.add(tuple);
            tuple.match = null;
        }
        Collections.shuffle(randomGroups, FIRE.server.getRandom());
        for (Tuple group : groups) {
            if (group.match != null) {
                continue;
            }
            int index = 0;
            boolean legal = false;
            Tuple match = null;
            while (!legal) {
                match = randomGroups.get(index++);
                boolean cp1 = false;
                for (int client : members.keySet()) {
                    if (group.members.contains(client)) {
                        cp1 = FIRE.server.getConfig(client).isCounterpart;
                    }
                }
                boolean cp2 = false;
                for (int client : members.keySet()) {
                    if (match.members.contains(client)) {
                        cp2 = FIRE.server.getConfig(client).isCounterpart;
                    }
                }
                legal = (group != match) && !(cp1 == cp2);
            }
            assert match != null;
            randomGroups.remove(group);
            randomGroups.remove(match);
            group.match = match;
            match.match = group;
        }
        // does setWorlds() need to be called after a shuffle?
    }

    public void newMessage(int secondsLeft, String message, String html, int senderID, String alias) {
        Tuple group = groupMap.get(senderID);
        for (int id : group.members) {
            ClientInterface client = members.get(id);
            client.newMessage(html);
        }
        mEvent.period = FIRE.server.getConfig().period;
        mEvent.secondsLeft = secondsLeft;
        mEvent.subject = senderID;
        mEvent.population = group.population;
        mEvent.alias = alias;
        mEvent.text = message;
        FIRE.server.commit(mEvent, "|");
    }
    /*
    private void setWorlds() {
    int curWorld = 1;
    for (Tuple tuple : tuples) {
    if (tuple.discovered == false) {
    tuple.discovered = true;
    tuple.pathDist = 0;
    int inWorld = discoverNext(tupleMap.get(tuple.match), curWorld, 1);
    if (inWorld > 0)
    tuple.world = curWorld;
    curWorld++;
    System.err.println("world " + curWorld + " contains member " + tuple.members.toArray()[0]);
    }
    }
    }

    private int discoverNext(Tuple tuple, int curWorld, int pathLength) {
    if (tuple.discovered == false) {
    tuple.pathDist = pathLength;
    tuple.discovered = true;
    int inWorld = discoverNext(tupleMap.get(tuple.match), curWorld, pathLength + 1);
    if (inWorld > 0)
    tuple.world = curWorld;
    System.err.println("world " + curWorld + " contains member " + tuple.members.toArray()[0]);
    return --inWorld;
    }
    else {
    return pathLength - tuple.pathDist;
    }
    }
     */

    private class StrategyUpdateEvent {

        public int id;
        public int changedId;
        public Map<Integer, float[]> strategies;
        public Map<Integer, float[]> matchStrategies;

        public StrategyUpdateEvent(int id, int changedId, Map<Integer, float[]> strategies, Map<Integer, float[]> matchStrategies) {
            this.id = id;
            this.changedId = changedId;
            this.strategies = strategies;
            this.matchStrategies = matchStrategies;
        }
    }

    private class StrategyUpdateProcessor extends Thread {

        private BlockingQueue<StrategyUpdateEvent> queue;

        public StrategyUpdateProcessor(BlockingQueue<StrategyUpdateEvent> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    StrategyUpdateEvent event = queue.take();
                    synchronized (logLock) {
                        if (event.strategies != null) {
                            members.get(event.id).setStrategies(event.changedId, event.strategies);
                        } else {
                            members.get(event.id).setMatchStrategies(event.changedId, event.matchStrategies);
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
