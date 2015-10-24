package org.watson.core.handler.message;

import org.watson.core.Watson;
import org.watson.protocol.io.DatabaseAdapter;
import org.watson.module.user.UserAccess;
import org.watson.protocol.IRCMessageHandler;
import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public final class LoginListener implements IRCMessageHandler {
    @Override
    public final void handle(IncomingMessage msg) {
        String[] parts = msg.getMessage().split(" ");
        if (parts.length == 4) {
            if (parts[1].equals("login")) {
                final String text = msg.getMessage().substring(msg.getMessage().indexOf(parts[1]));
                parts = text.split(" ");
                final String user = parts[1];
                final String pass = parts[2];
                UserAccess found = DatabaseAdapter.authenticateUser(user, pass);
                if (found != UserAccess.ANYONE) {
                    msg.getServer().getUserProperties().getUsers().put(msg.getHostName(), found);
                    Watson.getInstance().save();
                    msg.sendChat("Thank you for authenticating, you have been granted " + found + " access.");
                }
            }
        } else {
            msg.sendChat("To login: login <username> <password>");
        }
    }

    @Override
    public final boolean shouldHandle(IncomingMessage msg) {
        if (msg.isDestMe() && msg.getMessage().startsWith(msg.getServer().getUserProperties().getNickname())) {
            final String[] parts = msg.getMessage().split(" ");
            if (parts[1].equals("login")) {
                return true;
            }

        }
        return false;
    }

}
