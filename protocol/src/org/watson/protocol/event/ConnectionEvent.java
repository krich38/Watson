package org.watson.protocol.event;

import org.watson.protocol.IRCMessageHandler;
import org.watson.protocol.IRCServer;
import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 1.0
 *          <p>
 *          Look for when we are connected
 */
public class ConnectionEvent implements IRCMessageHandler {
    @Override
    public final void handle(IncomingMessage msg) {
        if (msg.getCommand().equalsIgnoreCase("001")) {
            final IRCServer server = msg.getServer();
            for (String s : server.getUserProperties().getHomeChannels()) {
                server.getChannel().write("JOIN #" + s);
            }
            server.getChannel().writeAndFlush("PRIVMSG NICKSERV :IDENTIFY " + msg.getServer().getUserProperties().getPassword());

            msg.getServer().getIrcClient().getOnConnected().handleCallBack();

        } else if (msg.getRaw().contains("No more connections allowed from your host via this connect class")) {
            msg.getServer().getUserProperties().setDoReconnect(false);
        }


    }

    @Override
    public final boolean shouldHandle(IncomingMessage msg) {
        return msg.getCommand().equalsIgnoreCase("001") || msg.getRaw().contains("No more connections allowed from your host via this connect class");
    }
}
