package org.watson.command;

import org.watson.command.handler.Say;
import org.watson.module.util.ClassEnumerator;
import org.watson.protocol.IRCClient;
import org.watson.protocol.IRCServer;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public class CommandManager {
    private final List<IRCClient> connections;
    private final Map<String, CommandActor> commandListeners;

    public CommandManager() {
        connections = new CopyOnWriteArrayList<>();
        commandListeners = new ConcurrentHashMap<>();
        commandListeners.clear();

    }

    public boolean load() {
        for (Class c : ClassEnumerator.getClassesForPackage(Say.class.getPackage())) {
            try {
                final Object o = c.newInstance();
                if (CommandActor.class.isAssignableFrom(c)) {
                    final CommandActor command = (CommandActor) o;
                    for (String s : command.getCommands().split(",")) {
                        commandListeners.put(s, command);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public final CommandActor getCommand(String command) {
        return commandListeners.get(command);
    }

    public final boolean hasCommand(String command) {
        return commandListeners.containsKey(command);
    }


    public final Map<String, CommandActor> getCommandActors() {
        return commandListeners;
    }

}
