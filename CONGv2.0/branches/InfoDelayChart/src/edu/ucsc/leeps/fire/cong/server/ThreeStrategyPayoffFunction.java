package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.cong.config.Config;
import java.util.Map;

/**
 *
 * @author jpettit
 */
public class ThreeStrategyPayoffFunction implements PayoffFunction {

    public String name;
    public float min;
    public float max;
    public float Rr, Rp, Rs,
            Pr, Pp, Ps,
            Sr, Sp, Ss;
    public float subperiodBonus;

    public ThreeStrategyPayoffFunction() {
        Rr = 0;
        Rp = 10;
        Rs = 60;
        Pr = 80;
        Pp = 5;
        Ps = 10;
        Sr = 20;
        Sp = 100;
        Ss = 10;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public int getNumStrategies() {
        return 3;
    }

    public float getSubperiodBonus(int subperiod, Config config) {
        return subperiodBonus;
    }

    public float getPayoff(
            int id,
            float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies,
            Config config) {
        if (matchPopStrategies.isEmpty()) {
            return 0;
        }
        float A, B, C, a, b, c;
        A = popStrategies.get(id)[0];
        B = popStrategies.get(id)[1];
        C = popStrategies.get(id)[2];
        float[] match = PayoffUtils.getAverageStrategy(id, matchPopStrategies);
        a = match[0];
        b = match[1];
        c = match[2];
        return A * (Rr * a + Rp * b + Rs * c)
                + B * (Pr * a + Pp * b + Ps * c)
                + C * (Sr * a + Sp * b + Ss * c);
    }

    public float[] getPopStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return PayoffUtils.getAverageStrategy(id, popStrategies);
    }

    public float[] getMatchStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return PayoffUtils.getAverageStrategy(id, matchPopStrategies);
    }

    public void configure(Config config) {
    }
}
