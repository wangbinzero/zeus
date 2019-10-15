package com.study.zeus.core;

import com.study.zeus.proto.Request;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket 服务端
 */
public abstract class AbstractWebsocketServer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWebsocketServer.class);
    private final Integer maxFrameSize = 2 << 20;
    private final Integer maxContentLength = 2 << 13;
    public static Map<String, NioSocketChannel> connetionPool = new ConcurrentHashMap<>();
    public static Map<String, NioSocketChannel> depthPool, klinePool, detailPool = new ConcurrentHashMap<>();
    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workGroup;

    public void init(int port, SimpleChannelInboundHandler handler) {
        try {
            bossGroup = new NioEventLoopGroup();
            workGroup = new NioEventLoopGroup();
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new HttpServerCodec());
                    pipeline.addLast(new ChunkedWriteHandler());
                    pipeline.addLast(new HttpObjectAggregator(maxContentLength));
                    pipeline.addLast(new WebSocketServerCompressionHandler());
                    pipeline.addLast(new WebSocketServerProtocolHandler("/", null, true, maxFrameSize));
                    pipeline.addLast(handler);
                }
            });

            Channel channel = server.bind(new InetSocketAddress(port)).sync().channel();
            if (channel.isOpen()) {
                logger.info("服务端启动成功,端口:[{}],连接方式: [{}]", port, "ws://ip:port/");
            }
        } catch (Exception e) {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workGroup != null) {
                workGroup.shutdownGracefully();
            }
        }

    }

    public abstract void run(int port);

    public abstract void onReceiveMessage(Request req, NioSocketChannel channel);

    public abstract void onLine(ChannelHandlerContext ctx);

    public abstract void offLine(ChannelHandlerContext ctx);
}
