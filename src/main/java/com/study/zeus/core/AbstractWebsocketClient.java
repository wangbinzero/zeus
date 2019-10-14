package com.study.zeus.core;

import com.study.zeus.core.utils.DateUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.LocalDateTime;

/**
 * websocket客户端
 */
public abstract class AbstractWebsocketClient {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWebsocketClient.class);
    private final Integer maxContentLength = 2 << 13;
    protected EventLoopGroup group;
    protected Channel channel;

    /**
     * 初始化
     *
     * @param uri     服务端地址
     * @param handler 处理器
     */
    protected void init(final URI uri, final SimpleChannelInboundHandler handler) throws Exception {
        String schema = uri.getScheme() == null ? "http" : uri.getScheme();
        final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        final int port;
        if (uri.getPort() == -1) {
            if ("http".equals(schema) || "ws".equals(schema)) {
                port = 80;
            } else if ("wss".equals(schema)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }

        final boolean ssl = "wss".equals(schema);
        final SslContext sslContext;
        if (ssl) {
            sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslContext = null;
        }
        group = new NioEventLoopGroup(2);
        Bootstrap client = new Bootstrap();
        client.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                if (sslContext != null) {
                    pipeline.addLast(sslContext.newHandler(ch.alloc(), host, port));
                }
                pipeline.addLast(new HttpClientCodec(), new HttpObjectAggregator(maxContentLength), handler);
            }
        });
        channel = client.connect(host, port).sync().channel();
        if (channel.isActive()) {
            logger.info("行情基类启动成功,时间:[{}]", DateUtil.format(LocalDateTime.now()));
        }
    }


    /**
     * 客户端启动时连接方法
     */
    public abstract void connect();

    /**
     * 客户端通过发送心跳包告诉服务器 当前客户端还活着
     * 否则服务端会踢掉客户端
     */
    public abstract void sendPing();

    /**
     * 创建订阅频道
     *
     * @param channel
     */
    public abstract void addChannel(String channel);

    /**
     * 移除已订阅的频道
     *
     * @param channel
     */
    public abstract void removeChannel(String channel);

    /**
     * 客户端短线，发起重连
     */
    public abstract void reConnect();

    /**
     * 消息处理
     *
     * @param message
     */
    public abstract void onReceiveMsg(String message);
}
