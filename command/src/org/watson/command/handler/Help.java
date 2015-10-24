package org.watson.command.handler;

import org.watson.command.CommandActor;
import org.watson.command.CommandManager;
import org.watson.protocol.IRCServer;
import org.watson.protocol.io.IncomingMessage;

import java.util.Iterator;

/**
 * @author Kyle Richards
 * @version 2.0
 *          <p>
 *          Get help on a certain command, or all
 */
public final class Help implements CommandActor {


    private final CommandManager manager;

    public Help() {
        manager = CommandManager.getCommandManager();
    }

    @Override
    public final void handleCommand(IRCServer server, String command, IncomingMessage message) {
        if (message.hasMessage()) {
            final String cmd = message.getMessage();
            if (manager.hasCommand(cmd)) {
                final String help = manager.getCommand(cmd).getHelp();
                message.sendChat(help != null ? help : cmd);
            }
        } else {
            message.sendChat(getHelp());
            String msg = "Available commands: ";
            final Iterator<String> iter = manager.getCommandListeners().keySet().iterator();
            while (iter.hasNext()) {
                msg = msg + iter.next();
                if (iter.hasNext()) {
                    msg = msg + ", ";
                }
            }
            message.sendChat(msg);
        }
    }

    @Override
    public final String getHelp() {
        return "Usage: help [command]";
    }

    @Override
    public final String getCommands() {
        return "help";
    }
}
