package org.watson.core.handler.message;

import org.watson.command.CommandManager;
import org.watson.command.handler.Markov;
import org.watson.command.io.MarkovDatabaseAdapter;
import org.watson.protocol.IRCMessageHandler;
import org.watson.protocol.io.IncomingMessage;

/**
 * @author Kyle Richards
 * @version 1.0
 *          <p>
 *          Watson observes every message sent into the channel and extracts information
 *          and updates the database.
 */
public final class MarkovListener implements IRCMessageHandler {

    @Override
    public final void handle(IncomingMessage message) {
        if (message.isDestChannel()) {
            MarkovDatabaseAdapter.markovLearn(message.getMessage());
            if (CommandManager.RANDOM.nextFloat() * 100 <= Markov.REPLY_RATE) {
                String markov = MarkovDatabaseAdapter.markovGenerate();
                if (markov != null) {
                    message.sendChat(markov);
                }
            }
        }
    }


}
