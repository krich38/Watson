package org.watson.command;

import org.watson.module.user.UserAccess;
import org.watson.protocol.IRCServer;
import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public interface CommandActor {
    public void handleCommand(IRCServer server, String command, IncomingMessage message);

    public String getHelp();

    public String getCommands();

    public default UserAccess getRequiredAccess() {
        return UserAccess.ANYONE;
    }
}
