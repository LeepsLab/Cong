package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.cong.client.Client;

/**
 *
 * @author jpettit
 */
public class Countdown extends Sprite {

    private int secondsLeft;

    public Countdown(Sprite parent, int x, int y, Client embed) {
        super(parent, x, y, (int) embed.textWidth("Seconds Left: 00"), (int) (embed.textAscent() + embed.textDescent()));
        secondsLeft = 0;
    }

    /**
     * Creates a string with the seconds left. Text is black. Aligns text to the
     * left. Draws text at the origin of applet.
     * @param applet Countdown applet.
     */
    @Override
    public void draw(Client applet) {
        String string = String.format("Seconds Left: %d", secondsLeft);
        applet.fill(0);
        applet.textAlign(Client.LEFT);
        applet.text(string, origin.x, origin.y);
    }

    /**
     * Uses secondsLeft to determine the amount of time in the period. 
     * @param secondsLeft How many seconds remain in the period.
     */
    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }
}
