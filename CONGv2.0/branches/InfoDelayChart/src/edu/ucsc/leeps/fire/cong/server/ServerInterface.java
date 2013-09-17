package edu.ucsc.leeps.fire.cong.server;

/**
 *
 * @author jpettit
 */
public interface ServerInterface {

    public void strategyChanged(
            float[] newStrategy,
            float[] targetStrategy,
            Integer id);

    public void newMessage(String message, int senderID);

}
