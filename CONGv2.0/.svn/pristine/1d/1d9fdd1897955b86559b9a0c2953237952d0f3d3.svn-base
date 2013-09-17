
import edu.ucsc.leeps.fire.cong.server.ScriptedPayoffFunction.PayoffScriptInterface;
import edu.ucsc.leeps.fire.cong.config.Config;
import java.util.*;

public class Hotelling implements PayoffScriptInterface {

    public float getPayoff(
            int id,
            float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies,
            Config config) {
        if (popStrategies.size() < 2) { // if only 1 person is playing, they get zero
            return 0;
        }
        
        SortedSet<Float> sorted = new TreeSet<Float>();
        for (float[] s : popStrategies.values()) {
            sorted.add(s[0]);
        }
        
        float s = popStrategies.get(id)[0];
        SortedSet<Float> leftSide = sorted.headSet(s);
        SortedSet<Float> rightSide = sorted.tailSet(s);
        rightSide.remove(s); // remove s from right side because tailSet is inclusive
        float left, right;
        if (leftSide.isEmpty()) {
            left = 0;
        } else {
            left = leftSide.last();
        }
        if (rightSide.isEmpty()) {
            right = 1f;
        } else {
            right = rightSide.first();
        }
        float u;
        if (left == 0) {
            u = s + 0.5f * (right - s);   
        } else if (right == 1f) {
            u = 0.5f * (s - left) + (1 - s); 
        } else {
            u = 0.5f * (s - left) + 0.5f * (right - s); 
        }
        
        int shared = 0; // shared must be at least 1 after the loop, as you have to share your own strategy
        for (int otherId : popStrategies.keySet()) {
            if (popStrategies.get(otherId)[0] == s) {
                shared++;
            }
        }
        assert shared >= 1;
        return config.get("Alpha") * 100 * (u / shared);
    }
}