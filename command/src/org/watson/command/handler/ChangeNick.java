package org.watson.command.handler;

import org.watson.command.CommandActor;
import org.watson.module.user.UserAccess;
import org.watson.protocol.IRCServer;
import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 1.0
 *          <p>
 *          Command Watson to change his nickname and respond to his new nick name
 */
public final class ChangeNick implements CommandActor {
    @Override
    public final void handleCommand(IRCServer server, String command, IncomingMessage message) {
        if (message.hasMessage()) {

            server.write("NICK " + message.getMessage());
            server.getUserProperties().nick = message.getMessage();

        }
    }

    @Override
    public final String getHelp() {
        return "Usage: nick [new-nickname]";
    }

    @Override
    public final String getCommands() {
        return "nick";
    }

    @Override
    public final UserAccess getRequiredAccess() {
        return UserAccess.FULL_USER;
    }
}
