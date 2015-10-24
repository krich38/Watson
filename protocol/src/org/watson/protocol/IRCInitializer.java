package org.watson.protocol;

/**
 * @author Kyle Richards
 * @version 1.0
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import org.watson.protocol.net.SSLTrustFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.List;

/**
 * @author Kyle Richards
 * @version 2.0
 */
public final class IRCInitializer extends ChannelInitializer<SocketChannel> {
    private final boolean isSSL;
    private static final StringDecoder STRING_DECODER = new StringDecoder();
    private static final StringEncoder STRING_ENCODER = new StringEncoder();

    public IRCInitializer(boolean isSSL) {
        this.isSSL = isSSL;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        final ChannelPipeline pipeline = channel.pipeline();
        if (isSSL) {
            final SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, SSLTrustFactory.getTrustManagers(), null);
            final SSLEngine engine = context.createSSLEngine();
            engine.setUseClientMode(true);
            pipeline.addLast("ssl", new SslHandler(engine));
        }
        pipeline.addLast("frameDecoder", new LineBasedFrameDecoder(8192));
        pipeline.addLast("stringDecoder", STRING_DECODER);
        pipeline.addLast("stringEncoder", STRING_ENCODER);
        pipeline.addLast("lineEncoder", new MessageToMessageEncoder<String>() {
            @Override
            protected void encode(ChannelHandlerContext ctx, String message, List<Object> out) throws Exception {
                if (!message.endsWith("\r\n")) {
                    message = message + "\r\n";
                }

                if (message.length() > IRCServer.MAX_LENGTH) {
                    // split message in 2
                    final String firstHalf = message.substring(0, IRCServer.MAX_LENGTH) + "\r\n";
                    final String secondHalf = message.substring(IRCServer.MAX_LENGTH + 1, message.length());
                    out.add(firstHalf);
                    out.add(secondHalf);
                } else {
                    out.add(message);
                }
            }
        });
        pipeline.addLast("handler", new IRCHandler());
    }
}
