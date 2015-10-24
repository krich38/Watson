package org.watson.command;

import org.watson.command.handler.Say;
import org.watson.command.io.MarkovDatabaseAdapter;
import org.watson.protocol.event.InitActor;
import org.watson.module.util.ClassEnumerator;
import org.watson.protocol.IRCClient;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Kyle Richards
 * @version 1.0
 *          <p>
 *          Manages Watson's user controllable commands
 */
public class CommandManager {
    private final List<IRCClient> connections;
    private final Map<String, CommandActor> commandListeners;

    public static final Random RANDOM = new Random();

    public CommandManager() {
        connections = new CopyOnWriteArrayList<>();
        commandListeners = new ConcurrentHashMap<>();


    }

    public boolean load() {
        for (Class c : ClassEnumerator.getClassesForPackage(Say.class.getPackage())) {
            try {
                final Object o = c.newInstance();
                if (CommandActor.class.isAssignableFrom(c)) {
                    final CommandActor command = (CommandActor) o;
                    /*
                     Does our command require set up?
                     */
                    if (InitActor.class.isAssignableFrom(c)) {
                        InitActor init = (InitActor) o;
                        //init.init();
                    }
                    for (String s : command.getCommands().split(",")) {
                        commandListeners.put(s, command);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return MarkovDatabaseAdapter.establishConnection();
    }


    public final CommandActor getCommand(String command) {
        return commandListeners.get(command);
    }

    public final boolean hasCommand(String command) {
        return commandListeners.containsKey(command);
    }

}
