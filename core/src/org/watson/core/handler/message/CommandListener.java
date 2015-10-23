package org.watson.core.handler.message;

import org.watson.core.Watson;
import org.watson.command.CommandActor;
import org.watson.command.CommandManager;
import org.watson.module.user.UserAccess;
import org.watson.protocol.IRCMessageHandler;
import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public class CommandListener implements IRCMessageHandler {

    private static final String COMMAND_PATTERN = "(\\S+?)(?:[,:]? (.+))?";
    private final CommandManager cmds;

    public CommandListener() {
        cmds = Watson.getInstance().getCommands();
    }

    @Override
    public final void handle(IncomingMessage msg) {
        final String[] parts = msg.getMessage().split(" ", 3);
        final String command = parts[1];
        final CommandActor cmd = cmds.getCommand(command);
        IncomingMessage newMessage;
        if (parts.length > 2) {
            newMessage = new IncomingMessage(msg.getServer(), msg.getRaw(), msg.getSource(), msg.getCommand(), msg.getTarget(), parts[2]);
        } else {
            newMessage = new IncomingMessage(msg.getServer(), msg.getRaw(), msg.getSource(), msg.getCommand(), msg.getTarget(), null);
        }
        cmd.handleCommand(msg.getServer(), command, newMessage);
    }

    @Override
    public final boolean shouldHandle(IncomingMessage msg) {
        if ((msg.isDestChannel() && msg.getMessage().matches("^" + msg.getServer().getUserProperties().getNickname() + "[:,]? .+")) || msg.isDestMe()) {
            final String text = msg.getMessage().substring(msg.getMessage().indexOf(' ') + 1);
            if (text.matches(COMMAND_PATTERN)) {
                final String command = text.split(" ")[0];
                if (cmds.hasCommand(command)) {
                    final CommandActor commandListener = cmds.getCommand(command);
                    if (commandListener.getRequiredAccess() != UserAccess.ANYONE) {
                        final String host = msg.getSource().split("@")[1];
                        final UserAccess userRights = msg.getServer().getUserProperties().getUsers().get(host);
                        if (userRights != null) {
                            if (userRights.ordinal() >= commandListener.getRequiredAccess().ordinal()) {
                                return true;
                            }
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
