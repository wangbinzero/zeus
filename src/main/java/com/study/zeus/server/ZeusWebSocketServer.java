package com.study.zeus.server;

import com.study.zeus.core.AbstractWebsocketServer;
import com.study.zeus.proto.Request;
import com.study.zeus.server.handler.ZeusHandler;
import com.study.zeus.service.KlineService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
            //TODO K线请求
        }
    }

    @Override
    public void onLine(ChannelHandlerContext ctx) {

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
}
