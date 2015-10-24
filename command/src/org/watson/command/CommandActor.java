package org.watson.command;

import org.watson.module.user.UserAccess;
import org.watson.protocol.IRCServer;
import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public interface CommandActor {
    void handleCommand(IRCServer server, String command, IncomingMessage message);

    String getHelp();

    String getCommands();

    default UserAccess getRequiredAccess() {
        return UserAccess.ANYONE;
    }
}
