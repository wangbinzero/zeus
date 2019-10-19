package com.study.zeus.core;

import com.alibaba.fastjson.JSON;
import com.study.zeus.proto.Request;
import com.study.zeus.proto.Response;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket 服务端
 */
public abstract class AbstractWebsocketServer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWebsocketServer.class);
    private final Integer maxFrameSize = 2 << 20;
    private final Integer maxContentLength = 2 << 13;


    /**
     * -------------------------
     * depthPool    盘口
     * klinePool    K线
     * detailPool   24小时成交
     * <p>
     * sub_channel 订阅频道
     * --------------------------
     */
    public static Map<String, NioSocketChannel> connetionPool = new ConcurrentHashMap<>();
    public static Map<String, NioSocketChannel> depthPool = new ConcurrentHashMap<>();
    public static Map<String, NioSocketChannel> klinePool = new ConcurrentHashMap<>();
    public static Map<String, NioSocketChannel> detailPool = new ConcurrentHashMap<>();
    private static Map<String, Set<String>> sub_channel = new ConcurrentHashMap<>();
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


    /**
     * 订阅频道
     *
     * @param clientId 客户端ID
     * @param chan     socket通道
     * @param pool     连接池
     * @param channel  订阅频道
     */
    public static void subChannel(String clientId, NioSocketChannel chan, Map<String, NioSocketChannel> pool, String... channel) {
        if (null != pool) {
            //判断非法操作
            NioSocketChannel conn = connetionPool.get(clientId);
            if (chan != conn) {
                chan.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.illegaError())));
                return;
            }
            pool.put(clientId, chan);
            Set<String> sets = sub_channel.get(clientId);

            if (null != sets) {
                for (int i = 0; i < channel.length; i++) {
                    sets.add(channel[i]);
                }
            } else {
                Set<String> newSet = new HashSet<>();
                for (int i = 0; i < channel.length; i++) {
                    newSet.add(channel[i]);
                }
                sub_channel.put(clientId, newSet);
            }
        }
    }


    /**
     * 取消订阅
     *
     * @param clientId 客户端ID
     * @param channel  订阅频道
     */
    public static void unSubChannel(String clientId, Map<String, NioSocketChannel> map, NioSocketChannel socketChannel, String... channel) {

        NioSocketChannel conn = connetionPool.get(clientId);
        if (conn != socketChannel) {
            socketChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.illegaError())));
            return;
        }

        Set<String> set = sub_channel.get(clientId);
        List<String> channList = Arrays.asList(channel);
        if (null != set) {
            for (int i = 0; i < channList.size(); i++) {
                set.remove(channList.get(i));
            }
        }
    }

    public static void unSubAllChannel(String clientId) {
        depthPool.remove(clientId);
        klinePool.remove(clientId);
        detailPool.remove(clientId);
        sub_channel.remove(clientId);
    }


    /**
     * 发送消息
     *
     * @param msg
     * @param channel
     * @param event
     * @param map
     */
    public static void senMessage(Object msg, String channel, String event, Map<String, NioSocketChannel> map) {
        if (null != map) {
            Iterator<Map.Entry<String, NioSocketChannel>> entryIterator = map.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, NioSocketChannel> item = entryIterator.next();
                String clientId = item.getKey();
                NioSocketChannel conn = item.getValue();
                Set<String> sets = sub_channel.get(clientId);
                if (null != sets) {
                    if (sets.contains(channel)) {
                        conn.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.sucess(msg, channel, event))));
                    }
                }
            }
        }
    }

    public abstract void run(int port);

    public abstract void onReceiveMessage(Request req, NioSocketChannel channel);

    public abstract void onLine(ChannelHandlerContext ctx);

    public abstract void offLine(ChannelHandlerContext ctx);
}
