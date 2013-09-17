package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.ClientInterface;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.logging.TickEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jpettit
 */
public class Population implements Serializable {

    private Map<Integer, ClientInterface> members;
    private long periodStartTime;
    private Set<Tuple> tuples;
    private Map<Integer, Tuple> tupleMap;

    public Population() {
        tuples = new HashSet<Tuple>();
        tupleMap = new HashMap<Integer, Tuple>();
    }

    public void configure(Map<Integer, ClientInterface> members) {
        this.members = members;
        tuples.clear();
        tupleMap.clear();
        setupTuples();
        if (FIRE.server.getConfig().preLength == 0) {
            setInitialStrategies();
        }
    }

    public void setPeriodStartTime(long timestamp) {
        periodStartTime = timestamp;
        for (Tuple tuple : tuples) {
            tuple.evalTime = timestamp;
            tuple.updateCounterparts();
        }
    }

    public void strategyChanged(
            float[] newStrategy,
            float[] targetStrategy,
            Integer changed, long timestamp) {
        tupleMap.get(changed).update(changed, newStrategy, targetStrategy, timestamp);
    }

    public void endSubperiod(int subperiod) {
        for (Tuple tuple : tuples) {
            tuple.endSubperiod(subperiod);
        }
    }

    public void endPeriod() {
        long timestamp = System.currentTimeMillis();
        for (Tuple tuple : tuples) {
            tuple.endPeriod(timestamp);
        }
    }

    public void logTick(int subperiod, int secondsLeft) {
        // Log the tick information
        int period = FIRE.server.getConfig().period;
        float length = FIRE.server.getConfig().length;
        float percent = (float) (length * secondsLeft) / (float) length;
        for (int member : members.keySet()) {
            TickEvent tick = new TickEvent();
            tick.period = period;
            tick.subject = member;
            tick.subperiod = subperiod;
            tick.secondsLeft = secondsLeft;
            Tuple tuple = tupleMap.get(member);
            tick.population = tuple.population;
            tick.world = tuple.world;
            tick.strategy = tuple.strategies.get(member);
            tick.target = tuple.targets.get(member);
            tick.match = tuple.match.population;
            if (tuple == tuple.match && FIRE.server.getConfig().excludeSelf) {
                tick.matchStrategy = tuple.strategyExclude.get(member);
            } else {
                tick.matchStrategy = tuple.match.strategy;
            }
            tick.pf = FIRE.server.getConfig(member).payoffFunction;
            tick.payoff = tick.pf.getPayoff(percent, tick.strategy, tick.matchStrategy);
            FIRE.server.commit(tick);
        }
    }

    private class Tuple {

        public int population;
        public int world;
        public Set<Integer> members;
        public long evalTime;
        public float[] strategy;
        public Map<Integer, float[]> strategyExclude;
        public Map<Integer, float[]> strategies;
        public Map<Integer, float[]> targets;
        public Tuple match;

        public Tuple() {
            this(tuples.size());
        }

        public Tuple(int population) {
            this.population = population;
            tuples.add(this);
            members = new HashSet<Integer>();
            strategies = new HashMap<Integer, float[]>();
            targets = new HashMap<Integer, float[]>();
            if (FIRE.server.getConfig().payoffFunction instanceof TwoStrategyPayoffFunction) {
                strategy = new float[2];
            } else {
                strategy = new float[3];
            }
            strategyExclude = new HashMap<Integer, float[]>();
        }

        public void update(int changed, float[] strategy, float[] target, long timestamp) {
            if (FIRE.server.getConfig().subperiods == 0) {
                evaluate(timestamp);
                match.evaluate(timestamp);
            }
            strategies.put(changed, strategy);
            targets.put(changed, target);
            mergeStrategies();
            if (FIRE.server.getConfig().subperiods == 0) {
                updateCounterparts();
            }
        }

        public void updateCounterparts() {
            if (this == this.match && FIRE.server.getConfig().excludeSelf) {
                for (int member : match.members) {
                    Population.this.members.get(member).setCounterpartStrategy(strategyExclude.get(member));
                }
            } else {
                for (int member : match.members) {
                    Population.this.members.get(member).setCounterpartStrategy(strategy);
                }
            }
        }

        public void mergeStrategies() {
            if (this == this.match && FIRE.server.getConfig().excludeSelf) {
                for (int member : members) {
                    mergeStrategies(member);
                }
            } else {
                for (int i = 0; i < strategy.length; i++) {
                    strategy[i] = 0;
                }
                for (int member : members) {
                    float[] s = strategies.get(member);
                    for (int i = 0; i < strategy.length; i++) {
                        strategy[i] += s[i];
                    }
                }
                for (int i = 0; i < strategy.length; i++) {
                    strategy[i] /= (float) members.size();
                }
            }
        }

        public void mergeStrategies(int exclude) {
            float[] s = new float[strategy.length];
            for (int i = 0; i < s.length; i++) {
                s[i] = 0;
            }
            for (int member : members) {
                if (member != exclude) {
                    float[] s1 = strategies.get(member);
                    for (int i = 0; i < s.length; i++) {
                        s[i] += s1[i];
                    }
                }
            }
            for (int i = 0; i < s.length; i++) {
                s[i] /= (members.size() - 1);
            }
            strategyExclude.put(exclude, s);
        }

        public void evaluate(long timestamp) {
            float percent = (timestamp - periodStartTime) / (FIRE.server.getConfig().length * 1000f);
            float percentElapsed = (timestamp - evalTime) / (FIRE.server.getConfig().length * 1000f);
            evaluate(percent, percentElapsed);
            evalTime = timestamp;
        }

        public void evaluate(float percent, float percentElapsed) {
            for (int member : members) {
                PayoffFunction u = FIRE.server.getConfig(member).payoffFunction;
                float[] otherStrategy;
                if (this == this.match && FIRE.server.getConfig().excludeSelf) {
                    otherStrategy = strategyExclude.get(member);
                } else {
                    otherStrategy = match.strategy;
                }
                float payoff = u.getPayoff(percent, strategies.get(member), otherStrategy);
                payoff *= percentElapsed;
                FIRE.server.addToPeriodPoints(member, payoff);
            }
        }

        public void endSubperiod(int subperiod) {
            float percentElapsed = 1f / FIRE.server.getConfig().subperiods;
            float percent = subperiod * percentElapsed;
            evaluate(percent, percentElapsed);
            match.evaluate(percent, percentElapsed);
            for (int member : members) {
                float[] otherStrategy;
                if (this == this.match && FIRE.server.getConfig().excludeSelf) {
                    otherStrategy = strategyExclude.get(member);
                } else {
                    otherStrategy = match.strategy;
                }
                Population.this.members.get(member).endSubperiod(subperiod, strategies.get(member), otherStrategy);
            }
            mergeStrategies();
            updateCounterparts();

        }

        public void endPeriod(long timestamp) {
            evaluate(timestamp);
            match.evaluate(timestamp);
        }
    }

    public void setupTuples() {
        if (FIRE.server.getConfig().numTuples == 1
                || (members.size() / FIRE.server.getConfig().tupleSize) == 1) {
            setupSinglePopTuples();
        } else {
            if (FIRE.server.getConfig().assignedTuples) {
                setupAssignedTuples();
            } else {
                setupRandomTuples();
            }
        }
    }

    /*
     * Constructs a single tuple comprising all subjects
     * Tuple is linked to itself
     */
    private void setupSinglePopTuples() {
        Tuple tuple = new Tuple();
        tuple.members = members.keySet();
        tuple.match = tuple;
        for (int member : members.keySet()) {
            Config def = FIRE.server.getConfig();
            Config config = FIRE.server.getConfig(member);
            config.isCounterpart = false;
            config.payoffFunction = def.payoffFunction;
            config.counterpartPayoffFunction = def.payoffFunction;
            tupleMap.put(member, tuple);
            config.playersInTuple = members.size();
        }
    }

    private void setupAssignedTuples() {
        for (int member : members.keySet()) {
            Config config = FIRE.server.getConfig(member);
            int population = config.population;
            int match = config.match;
            Tuple p = null;
            Tuple m = null;
            for (Tuple tuple : tuples) {
                if (tuple.population == population) {
                    p = tuple;
                }
                if (tuple.population == match) {
                    m = tuple;
                }
            }
            if (p == null) {
                p = new Tuple(population);
            }
            if (m == null) {
                m = new Tuple(match);
            }
            p.members.add(member);
            p.match = m;
            tupleMap.put(member, p);
        }
        Set<Tuple> assignedMatches = new HashSet<Tuple>();
        Config def = FIRE.server.getConfig();
        for (Tuple tuple : tuples) {
            if (assignedMatches.contains(tuple)) {
                continue;
            }
            if (tuple.population > tuple.match.population) {
                tuple = tuple.match;
            }
            for (int member : tuple.members) {
                Config config = FIRE.server.getConfig(member);
                config.isCounterpart = false;
                config.payoffFunction = def.payoffFunction;
                config.counterpartPayoffFunction = def.counterpartPayoffFunction;
                config.playersInTuple = tuple.members.size();
            }
            for (int member : tuple.match.members) {
                Config config = FIRE.server.getConfig(member);
                config.isCounterpart = true;
                config.payoffFunction = def.counterpartPayoffFunction;
                config.counterpartPayoffFunction = def.payoffFunction;
                config.playersInTuple = tuple.match.members.size();
            }
            assignedMatches.add(tuple);
            assignedMatches.add(tuple.match);
        }
    }

    private void setupRandomTuples() {
        ArrayList<Integer> randomMembers = new ArrayList<Integer>();
        randomMembers.addAll(members.keySet());
        Collections.shuffle(randomMembers, FIRE.server.getRandom());
        if (FIRE.server.getConfig().tupleSize == -1) {
            FIRE.server.getConfig().tupleSize = members.size() / FIRE.server.getConfig().numTuples;
        }
        Tuple current = null;
        ArrayList<Tuple> randomTuples = new ArrayList<Tuple>();
        while (randomMembers.size() > 0) {
            if (current == null || current.members.size() == FIRE.server.getConfig().tupleSize) {
                current = new Tuple();
                randomTuples.add(current);
            }
            int member = randomMembers.remove(0);
            current.members.add(member);
            tupleMap.put(member, current);
        }
        Collections.shuffle(randomTuples, FIRE.server.getRandom());
        while (randomTuples.size() > 0) {
            Tuple tuple = randomTuples.remove(0);
            tuple.match = randomTuples.remove(0);
            tuple.match.match = tuple;
            Config def = FIRE.server.getConfig();
            Tuple tuple1;
            if (FIRE.server.getRandom().nextBoolean()) {
                tuple1 = tuple;
            } else {
                tuple1 = tuple.match;
            }
            for (int member : tuple1.members) {
                Config config = FIRE.server.getConfig(member);
                config.isCounterpart = false;
                config.payoffFunction = def.payoffFunction;
                config.counterpartPayoffFunction = def.counterpartPayoffFunction;
                config.playersInTuple = tuple1.members.size();
            }
            for (int member : tuple1.match.members) {
                Config config = FIRE.server.getConfig(member);
                config.isCounterpart = true;
                config.payoffFunction = def.counterpartPayoffFunction;
                config.counterpartPayoffFunction = def.payoffFunction;
                config.playersInTuple = tuple1.match.members.size();
            }
        }
        assert (randomMembers.size() == 0);
        assert (randomTuples.size() == 0);
    }

    private void setInitialStrategies() {
        for (int client : members.keySet()) {
            float[] s;
            if (FIRE.server.getConfig().payoffFunction instanceof TwoStrategyPayoffFunction) {
                s = new float[2];
                if (FIRE.server.getConfig().mixedStrategySelection) {
                    s[0] = FIRE.server.getRandom().nextFloat();
                } else {
                    s[0] = FIRE.server.getRandom().nextBoolean() ? 1 : 0;
                }
                s[1] = 1 - s[0];
            } else {
                s = new float[3];
                if (FIRE.server.getConfig().mixedStrategySelection) {
                    s[0] = FIRE.server.getRandom().nextFloat();
                    s[1] = (1 - s[0]) * FIRE.server.getRandom().nextFloat();
                    s[2] = 1 - s[0] - s[1];
                } else {
                    s[0] = 0;
                    s[1] = 0;
                    s[2] = 0;
                    s[FIRE.server.getRandom().nextInt(3)] = 1;
                }
            }
            FIRE.server.getConfig(client).initialStrategy = s;
        }
        for (Tuple tuple : tuples) {
            for (int member : tuple.members) {
                tuple.strategies.put(member, FIRE.server.getConfig(member).initialStrategy);
                tuple.targets.put(member, FIRE.server.getConfig(member).initialStrategy);
            }
            tuple.mergeStrategies();
        }
    }
}
