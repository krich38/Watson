package org.watson.protocol.event;

import org.watson.protocol.IRCServer;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public abstract class InitActor implements ProtocolEvent {

    private final IRCServer server;

    public void handleCallBack() {
        init();
    }

    public abstract void init();

    public InitActor(IRCServer server) {

        this.server = server;
    }

    public IRCServer getServer() {
        return server;
    }
}
