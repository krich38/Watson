package org.watson.core;

import org.watson.command.CommandManager;
import org.watson.core.handler.io.DatabaseAdapter;
import org.watson.core.handler.message.CommandListener;
import org.watson.module.ServerProperties;
import org.watson.module.util.ClassEnumerator;
import org.watson.protocol.IRCClient;
import org.watson.protocol.IRCMessageHandler;
import org.watson.protocol.IRCServer;
import org.watson.protocol.event.ProtocolEvent;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kyle Richards (Decisional Support System Engineer)
 *         Microsoft Australia
 *         Department of Computer Science
 */
public class Watson {


    private List<IRCClient> CONNECTED;
    private List<ServerProperties> UNCONNECTED;
    private CommandManager commands;

    public static Watson getInstance() {
        return INSTANCE;
    }

    private boolean loadAndSetup() throws FileNotFoundException {
        File[] files = new File("servers/").listFiles();
        if (files != null) {
            UNCONNECTED = new ArrayList<ServerProperties>(files.length);
            Yaml yaml = new Yaml();
            for (File f : files) {
                ServerProperties server = yaml.loadAs(new FileInputStream(f), ServerProperties.class);
                UNCONNECTED.add(server);
            }
        }

        commands = new CommandManager();
        return commands.load();
    }

    public Watson() throws FileNotFoundException {
        if (loadAndSetup()) {
            if (DatabaseAdapter.establishConnection()) {
                connectAll();
            }

        }
    }

    private void connectAll() {
        CONNECTED = new ArrayList<>();
        for (ServerProperties sc : UNCONNECTED) {

            final IRCClient client = new IRCClient(sc);
            client.connect();
            client.setOnConnected(new ProtocolEvent() {

                @Override
                public void handleCallBack() {
                    for (Class c : ClassEnumerator.getClassesForPackage(CommandListener.class.getPackage())) {
                        try {
                            IRCMessageHandler message = (IRCMessageHandler) c.newInstance();
                            client.attachMessageHandler(message);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    UNCONNECTED.remove(sc);
                    CONNECTED.add(client);
                }
            });
        }
    }

    private static Watson INSTANCE;

    public static void main(String[] args) {
        try {
            INSTANCE = new Watson();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public CommandManager getCommands() {
        return commands;
    }

    public void save() {

        for (IRCClient irc : CONNECTED) {
            ServerProperties up = irc.getConfig();
            try {
                Yaml yaml = new Yaml();
                yaml.dump(up, new FileWriter("servers/" + up.file));
                System.out.println(up.users);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<IRCClient> getConnected() {
        return CONNECTED;
    }
}
