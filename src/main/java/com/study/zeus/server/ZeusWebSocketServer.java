package com.study.zeus.server;

import com.alibaba.fastjson.JSON;
import com.study.zeus.core.AbstractWebsocketServer;
import com.study.zeus.entity.KlineDO;
import com.study.zeus.proto.Request;
import com.study.zeus.proto.Response;
import com.study.zeus.server.handler.ZeusHandler;
import com.study.zeus.service.KlineService;
import com.study.zeus.utils.RemoteUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ZeusWebSocketServer extends AbstractWebsocketServer {

    private static final Logger logger = LoggerFactory.getLogger(ZeusWebSocketServer.class);
    private KlineService klineService;

    public ZeusWebSocketServer(KlineService klineService) {
        this.klineService = klineService;
    }


    @Override
    public void run(int port) {
        ZeusHandler handler = new ZeusHandler(this, klineService);
        init(port, handler);
    }

    @Override
    public void onReceiveMessage(Request req, NioSocketChannel socketChannel) {
        String event = req.getEvent();
        String type = req.getType();
        String[] channel = req.getChannel();
        if (event.equalsIgnoreCase("sub")) {
            switch (type) {
                case "detail":
                    addChannel(channel, detailPool, socketChannel);
                    break;
                case "depth":
                    addChannel(channel, depthPool, socketChannel);
                    break;
            }
        } else if (event.equalsIgnoreCase("un_sub")) {
            switch (type) {
                case "detail":
                    logger.info("取消订阅");
                    removeChannelFromPool(channel, detailPool);
                    break;
                case "depth":
                    removeChannelFromPool(channel, depthPool);
                    break;
            }
        } else if (event.equalsIgnoreCase("req")) {
            klineEvent(socketChannel, channel[0]);
        }
    }

    @Override
    public void onLine(ChannelHandlerContext ctx) {
        logger.info("客户端:[{}] 上线", RemoteUtil.parseRemoteAddress(ctx.channel()));
        allocUUID(ctx);
    }

    @Override
    public void offLine(ChannelHandlerContext ctx) {
        logger.info("客户端下线了");
        Channel channel = ctx.channel();
        removeAllSubChannel((NioSocketChannel) channel, depthPool);
        removeAllSubChannel((NioSocketChannel) channel, klinePool);
        removeAllSubChannel((NioSocketChannel) channel, detailPool);
    }

    /**
     * 从连接池中删除
     *
     * @param channel
     */
    private void removeAllSubChannel(NioSocketChannel channel, Map<String, NioSocketChannel> map) {
        if (null != map) {
            Iterator<Map.Entry<String, NioSocketChannel>> entryIterator = map.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, NioSocketChannel> entry = entryIterator.next();
                NioSocketChannel value = entry.getValue();
                String key = entry.getKey();
                if (channel == value) {
                    connetionPool.remove(key);
                }
            }
        }
    }

    /**
     * 订阅频道
     *
     * @param channels
     * @param map
     * @param socketChannel
     */
    private void addChannel(String[] channels, Map<String, NioSocketChannel> map, NioSocketChannel socketChannel) {
        StringBuilder sb = new StringBuilder();
        for (String channel : channels) {
            map.put(channel, socketChannel);
            sb.append(channel + ",");
        }
        logger.info("客户端:[{}],订阅频道成功:[{}]", socketChannel.remoteAddress(), sb.toString());
    }

    /**
     * 为客户端分配UUID 并写入连接池
     *
     * @param ctx
     */
    private void allocUUID(ChannelHandlerContext ctx) {
        String uuid = UUID.randomUUID().toString();
        logger.info("客户端:[{}],UUID:[{}]", RemoteUtil.parseRemoteAddress(ctx.channel()), uuid);
        ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.sucess(uuid, "conn", "conn"))));
        connetionPool.put(uuid, (NioSocketChannel) ctx.channel());
    }


    /**
     * 取消订阅
     *
     * @param channels
     * @param map
     */
    private void removeChannelFromPool(String[] channels, Map<String, NioSocketChannel> map) {
        StringBuilder sb = new StringBuilder();
        NioSocketChannel socketChannel = null;
        Iterator<Map.Entry<String, NioSocketChannel>> entryIterator = map.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, NioSocketChannel> entry = entryIterator.next();
            String key = entry.getKey();
            List<String> result = Arrays.asList(channels);
            if (result.contains(key)) {
                socketChannel = entry.getValue();
                map.remove(key);
                sb.append(key + ",");
            }
        }
        logger.info("客户端:[{}],取消订阅频道成功:[{}]", socketChannel.remoteAddress(), sb.toString());
    }


    /**
     * K线事件
     *
     * @param nioSocketChannel
     * @param channel
     */
    private void klineEvent(NioSocketChannel nioSocketChannel, String channel) {
        //channel: request.kline.btcusdt.1min.init
        //channel: request.kline.btcusdt.1min.page.xxxxxx
        String[] str = channel.split("\\.");
        String symbol = str[2];
        String kType = str[3];
        String init = str[4];
        List<KlineDO> list = new ArrayList<>();
        if (init.equals("init")) {
            list = klineService.queryKline(kType, symbol);
        } else if (init.equals("page")) {
            long from = Long.valueOf(str[5]);
            list = klineService.queryFrom(from, symbol, kType);
        }
        nioSocketChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.sucess(list, channel, "req"))));
    }
}
