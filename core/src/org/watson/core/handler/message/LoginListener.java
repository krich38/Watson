package org.watson.core.handler.message;

import org.watson.protocol.IRCMessageHandler;
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
