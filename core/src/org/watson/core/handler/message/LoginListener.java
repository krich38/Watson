package org.watson.core.handler.message;

import org.watson.core.Watson;
import org.watson.core.handler.io.DatabaseAdapter;
import org.watson.module.user.UserAccess;
import org.watson.protocol.IRCClient;
import org.watson.protocol.IRCMessageHandler;
import org.watson.protocol.IRCServer;
import org.watson.protocol.io.IncomingMessage;

import java.util.Arrays;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public class LoginListener implements IRCMessageHandler {
    @Override
    public void handle(IncomingMessage msg) {
        String[] parts = msg.getMessage().split(" ");
        System.out.println(Arrays.toString(parts));
        if (parts.length == 4) {

            if (parts[1].equals("login")) {
                final String text = msg.getMessage().substring(msg.getMessage().indexOf(parts[1]));

                parts = text.split(" ");
                String user = parts[1];
                String pass = parts[2];
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
    public boolean shouldHandle(IncomingMessage msg) {
        if (msg.isDestMe() && msg.getMessage().startsWith(msg.getServer().getUserProperties().getNickname())) {
            String[] parts = msg.getMessage().split(" ");
            if (parts[1].equals("login")) {
                return true;
            }

        }
        return false;
    }

}
