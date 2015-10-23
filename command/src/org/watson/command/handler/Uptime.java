package org.watson.command.handler;


import org.watson.command.CommandActor;
import org.watson.protocol.IRCServer;
import org.watson.protocol.io.IncomingMessage;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

/**
 * @author Kyle Richards
 * @version 2.0
 */
public final class Uptime implements CommandActor {
    @Override
    public void handleCommand(IRCServer server, String command, IncomingMessage message) {
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        final long millis = mxBean.getUptime();
        final long second = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        final long minute = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        final long hour = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        final long days = TimeUnit.MILLISECONDS.toDays(millis) % 365;
        String msg = second + " seconds.";
        if (minute > 0) {
            msg = minute + " minutes, " + msg;
        }
        if (hour > 0) {
            msg = hour + " hours, " + msg;
        }
        if (days > 0) {
            msg = days + " days, " + msg;
        }
        message.sendChat("Current uptime: " + msg);
    }

    @Override
    public String getHelp() {
        return "Usage: uptime";
    }

    @Override
    public String getCommands() {
        return "uptime";
    }
}
