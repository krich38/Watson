package org.watson.command;

import org.watson.command.handler.Say;
import org.watson.command.io.MarkovDatabaseAdapter;
import org.watson.module.util.ClassEnumerator;
import org.watson.protocol.event.InitActor;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kyle Richards
 * @version 1.0
 *          <p>
 *          Manages Watson's user controllable commands
 *          Just a basic Singleton design
 */
public class CommandManager {

    private static CommandManager INSTANCE;
    private final Map<String, CommandActor> commandListeners;

    public static final Random RANDOM = new Random();

    public CommandManager() {

        commandListeners = new ConcurrentHashMap<>();
        if (!MarkovDatabaseAdapter.setup()) {
            // oh no
        }

    }

    public static CommandManager getCommandManager() {
        if (INSTANCE == null) {
            INSTANCE = new CommandManager();
        }
        return INSTANCE;
    }

    public boolean load() {
        commandListeners.clear();
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
                        init.init();
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
        return true;
    }


    public final CommandActor getCommand(String command) {
        return commandListeners.get(command);
    }

    public final boolean hasCommand(String command) {
        return commandListeners.containsKey(command);
    }

    public Map<String, CommandActor> getCommandListeners() {
        return commandListeners;
    }
}
