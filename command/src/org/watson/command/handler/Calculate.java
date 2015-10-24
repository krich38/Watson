package org.watson.command.handler;

import org.watson.command.CommandActor;
import org.watson.command.util.Expression;
import org.watson.protocol.IRCServer;
import org.watson.protocol.io.IncomingMessage;

import java.math.BigDecimal;

/**
 * @author Kyle Richards
 * @version 2.0
 *          <p>
 *          Command Watson to calculate a mathematical function
 */
public final class Calculate implements CommandActor {
    @Override
    public final void handleCommand(IRCServer server, String command, IncomingMessage message) {
        try {
            final Expression expression = new Expression(message.getMessage());
            final BigDecimal result = expression.eval();
            message.sendChat(result.toPlainString());
        } catch (Exception e) {
            message.sendChatf("Calc error: %s", e.getMessage());
        }
    }

    @Override
    public final String getHelp() {
        return "Usage: calc [expression], e.g calc 5 + 7";
    }

    @Override
    public final String getCommands() {
        return "calc";
    }
}
