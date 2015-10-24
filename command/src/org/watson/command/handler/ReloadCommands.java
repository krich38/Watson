package org.watson.command.handler;

import org.watson.command.CommandActor;
import org.watson.command.CommandManager;
import org.watson.module.user.UserAccess;
import org.watson.protocol.IRCServer;
import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 2.0
 *          <p>
 *          Reload the commands
 */
public final class ReloadCommands implements CommandActor {
    @Override
    public final void handleCommand(IRCServer server, String command, IncomingMessage message) {
        if (CommandManager.getCommandManager().load()) {
            message.sendChat("Commands reloaded.");
        } else {
            message.sendChat("I can't :(");
        }
    }

    @Override
    public final String getHelp() {
        return "Usage: rlcmds";
    }

    @Override
    public final String getCommands() {
        return "rlcmds";
    }

    @Override
    public final UserAccess getRequiredAccess() {
        return UserAccess.FULL_USER;
    }
}
