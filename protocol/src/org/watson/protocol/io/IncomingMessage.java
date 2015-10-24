package org.watson.protocol.io;

import org.watson.protocol.IRCServer;

/**
 * @author Kyle Richards
 * @version 1.0
 *          <p>
 *          Represents every incoming message from the IRC Server
 */
public final class IncomingMessage {
    private final String raw;
    private final String source;
    private final String command;
    private final String target;
    private final String targetParams;
    private String sender;
    private String message;
    private final IRCServer server;
    private String host;

    public IncomingMessage(IRCServer server, String raw, String source, String command, String target, String message) {
        this.server = server;
        this.raw = raw;
        this.source = source;
        this.command = command;
        if (command.equals("PRIVMSG")) {
            this.sender = source.substring(0, source.indexOf("!"));
            this.host = source.substring(source.indexOf("@") + 1);
        }
        if (target != null) {
            final String[] dummy = target.split(" ", 2);
            this.target = dummy[0];
            this.targetParams = (dummy.length == 2 ? dummy[1] : null);
        } else {
            this.target = this.targetParams = null;
        }
        if (message == null) {
            this.message = "";
        } else {
            this.message = message.trim();
        }
    }

    public final String getRaw() {
        return raw;
    }

    public final String getSource() {
        return source;
    }

    public final String getCommand() {
        return command;
    }

    public final String getTarget() {
        return target;
    }

    public final String getTargetParams() {
        return targetParams;
    }

    public final String getMessage() {
        return message;
    }

    public final IRCServer getServer() {
        return server;
    }

    public final boolean isDestChannel() {
        return getCommand().equalsIgnoreCase("PRIVMSG") && getTarget().startsWith("#");
    }

    public final boolean isDestMe() {
        return getCommand().equalsIgnoreCase("PRIVMSG") && getTarget().equals(server.getUserProperties().getNickname());
    }

    private String getSendDest() {
        if (isDestChannel()) {
            return getTarget();
        } else {
            return sender;
        }
    }

    public final void setMessage(String message) {
        this.message = message;
    }

    public final void sendChatf(String message, Object... format) {
        sendChat(String.format(message, format));
    }

    public final void sendChat(String message) {
        server.getChannel().writeAndFlush("PRIVMSG " + getSendDest() + " :" + message);
    }

    public final boolean hasMessage() {
        return message != null && !message.isEmpty();
    }

    public String getSender() {
        return sender;
    }

    public String getHostName() {
        return host;
    }
}
