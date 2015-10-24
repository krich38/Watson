package org.watson.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.watson.module.util.ClassEnumerator;
import org.watson.protocol.event.ConnectionEvent;
import org.watson.protocol.io.IncomingMessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kyle Richards
 * @version 1.0
 */
public class IRCHandler extends SimpleChannelInboundHandler<String> {
    public static final Pattern IRC_PATTERN = Pattern.compile("^(?:[:](\\S+) )?(\\S+)(?: (?!:)(.+?))?(?: [:](.*))?$");
    private final List<IRCMessageHandler> messageHandlers;

    public IRCHandler() {
        messageHandlers = new CopyOnWriteArrayList<>();
        for (Class c : ClassEnumerator.getClassesForPackage(ConnectionEvent.class.getPackage())) {
            try {
                if (IRCMessageHandler.class.isAssignableFrom(c)) {
                    IRCMessageHandler message = (IRCMessageHandler) c.newInstance();
                    messageHandlers.add(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext context, String msg) throws Exception {
        Matcher m = IRC_PATTERN.matcher(msg);
        if (!m.matches()) {
            System.out.println("Unmatched: " + msg);
            return;
        }
        IncomingMessage message = new IncomingMessage(new IRCServer(context.channel()), msg, m.group(1), m.group(2), m.group(3), m.group(4));
        for (IRCMessageHandler handler : messageHandlers) {
            if (handler.shouldHandle(message)) {
                handler.handle(message);
            }
        }
        if (message.getServer().getIrcClient().isLogging()) {
            System.out.println(message.getRaw());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel c = ctx.channel();
        IRCServer server = new IRCServer(c);

        c.write("NICK " + server.getUserProperties().getNickname());
        c.writeAndFlush("USER " + server.getUserProperties().getIdent() + " 0 * :" + server.getUserProperties().getRealname());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        IRCServer server = new IRCServer(ctx.channel());
        //engine.onException(server, cause);
        cause.printStackTrace();
    }

    public void attachMessageHandler(IRCMessageHandler messageHandler) {
        messageHandlers.add(messageHandler);
    }
}
