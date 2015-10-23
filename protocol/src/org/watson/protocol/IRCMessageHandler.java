package org.watson.protocol;

import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public interface IRCMessageHandler {
    public void handle(IncomingMessage msg);

    public default boolean shouldHandle(IncomingMessage msg) {
        return true;
    }
}
