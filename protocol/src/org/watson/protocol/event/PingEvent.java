package org.watson.protocol.event;

import org.watson.protocol.IRCMessageHandler;
import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public class PingEvent implements IRCMessageHandler {
    @Override
    public final boolean shouldHandle(IncomingMessage message) {
        return message.getCommand().equalsIgnoreCase("PING");
    }

    @Override
    public final void handle(IncomingMessage message) {
        message.getServer().getChannel().writeAndFlush("PONG :" + message.getMessage());
    }
}

