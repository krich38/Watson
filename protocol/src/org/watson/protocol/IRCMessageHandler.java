package org.watson.protocol;

import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public interface IRCMessageHandler {
    public void handle(IncomingMessage msg);

    public boolean shouldHandle(IncomingMessage msg);
}
