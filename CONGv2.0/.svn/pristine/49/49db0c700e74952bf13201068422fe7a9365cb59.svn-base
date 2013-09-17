package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.cong.config.Config;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author dev
 */
public interface PayoffFunction extends Serializable {

    public float getMin();

    public float getMax();

    public int getNumStrategies();

    public float getSubperiodBonus(int subperiod, Config config);

    public float getPayoff(
            int id, float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies,
            Config config);

    public float[] getPopStrategySummary(
            int id, float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies);

    public float[] getMatchStrategySummary(
            int id, float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies);

    public void configure();
}
