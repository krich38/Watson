package org.watson.command.handler;


import org.watson.command.CommandActor;

import org.watson.module.user.UserAccess;
import org.watson.protocol.IRCServer;
import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 2.0
 *          <p>
 *          Command Watson to say something
 */
public final class Say implements CommandActor {
    @Override
    public final void handleCommand(IRCServer server, String command, IncomingMessage message) {
        if (message.hasMessage()) {
            String msg = message.getMessage();
            String target = message.getTarget();
            final String[] parts = message.getMessage().split(" ", 2);
            /*
             Is the user asking Watson to say this in a channel?
             */
            if (parts[0].startsWith("{") && parts[0].endsWith("}") && parts.length > 1) {
                target = parts[0].replace("{", "").replace("}", "");
                msg = parts[1];
            }

            server.sendMessage(target, msg);

        }
    }

    @Override
    public final String getHelp() {
        return "Usage: say {#channel} [text to say]   OR    say [text to say]";
    }

    @Override
    public final String getCommands() {
        return "say";
    }

    @Override
    public final UserAccess getRequiredAccess() {
        return UserAccess.FULL_USER;
    }
}
