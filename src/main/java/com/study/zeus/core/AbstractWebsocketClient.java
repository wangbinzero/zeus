package com.study.zeus.core;

import com.google.common.collect.Sets;
import com.study.zeus.utils.DateUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * websocket客户端
 */
public abstract class AbstractWebsocketClient {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWebsocketClient.class);
    private final Integer maxContentLength = 2 << 13;
    protected Set<String> subChannel = Sets.newHashSet();
    protected EventLoopGroup group;
    protected Channel channel;
    protected MonitorThread monitorThread;
    private ScheduledExecutorService executorService;

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
                pipeline.addLast(new HttpClientCodec(), new HttpObjectAggregator(maxContentLength),
                        WebSocketClientCompressionHandler.INSTANCE,
                        handler);
            }
        });
        channel = client.connect(host, port).sync().channel();
        if (channel.isActive()) {
            logger.info("行情基类启动成功,时间:[{}]", DateUtil.format(LocalDateTime.now()));
        }
    }

    public boolean isAlive() {
        return this.channel != null && this.channel.isActive();
    }

    public void sendMessage(String message) {
        if (!isAlive()) {
            return;
        }
        this.channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    public void close() {
        monitorThread = null;
        executorService.shutdown();
    }

    public void start() {
        this.connect();
        monitorThread = new MonitorThread(this);
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(monitorThread, 0, 5000, TimeUnit.MILLISECONDS);
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
