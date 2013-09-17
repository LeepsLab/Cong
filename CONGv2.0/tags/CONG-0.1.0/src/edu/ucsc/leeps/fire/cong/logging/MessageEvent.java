package edu.ucsc.leeps.fire.cong.logging;

import edu.ucsc.leeps.fire.logging.LogEvent;

public class MessageEvent implements LogEvent {

    public String period;
    public int subperiod;
    public int secondsLeft;
    public int subject;
    public int population;
    public String alias;
    public String text;
}
