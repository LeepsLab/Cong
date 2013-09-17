package edu.ucsc.leeps.fire.cong.server;

/**
 *
 * @author jpettit
 */
public class ThreeStrategyPayoffFunction implements PayoffFunction {

    public float min;
    public float max;
    public float Rr, Rp, Rs,
            Pr, Pp, Ps,
            Sr, Sp, Ss;

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

    public float getPayoff(
            float percent,
            float[] myStrategy, float[] opponentStrategy) {
        float A, B, C, a, b, c;
        A = myStrategy[0];
        B = myStrategy[1];
        C = myStrategy[2];
        a = opponentStrategy[0];
        b = opponentStrategy[1];
        c = opponentStrategy[2];
        return A * (Rr * a + Rp * b + Rs * c)
                + B * (Pr * a + Pp * b + Ps * c)
                + C * (Sr * a + Sp * b + Ss * c);
    }
}
