package com.study.zeus.huobi;

import com.alibaba.fastjson.JSONObject;
import com.study.zeus.core.AbstractWebsocketClient;
import com.study.zeus.huobi.service.HuobiService;
import com.study.zeus.utils.DateUtil;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

public class HuobiClient extends AbstractWebsocketClient {

    private static final Logger logger = LoggerFactory.getLogger(HuobiClient.class);
    private String url;
    private HuobiService service;
    private String subId = "";

    public HuobiClient(String url, HuobiService service) {
        this.url = url;
        this.service = service;
    }

    @Override
    public void connect() {
        try {
            subId = UUID.randomUUID().toString();
            final URI uri = URI.create(url);
            HuobiHandler handler = new HuobiHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()), this);
            init(uri, handler);
            if (isAlive()) {
                handler.channelPromise().sync();
            }
        } catch (Exception e) {
            logger.error("火币客户端启动出错: [{}]", e.getMessage());
            e.printStackTrace();
            if (group != null) {
                group.shutdownGracefully();
            }
        }
    }

    public void addSub(String channel) {
        if (!isAlive()) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sub", channel);
        jsonObject.put("id", subId);
        String message = jsonObject.toString();
        this.sendMessage(message);
        this.addChannel(channel);
    }

    public void sendPong(long pong) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pong", pong);
        this.sendMessage(jsonObject.toString());
    }

    @Override
    public void sendPing() {
        JSONObject json = new JSONObject();
        json.put("ping", System.currentTimeMillis());
        this.sendMessage(json.toString());
    }

    @Override
    public void addChannel(String channel) {
        if (null == channel) {
            return;
        }
        subChannel.add(channel);
    }

    @Override
    public void removeChannel(String channel) {
        if (null != channel) {
            subChannel.remove(channel);
        }
    }

    @Override
    public void reConnect() {
        logger.info("火币客户端开始重连,当前时间:[{}]", DateUtil.format(LocalDateTime.now()));
        if (group != null) {
            group.shutdownGracefully();
        }
        this.group = null;
        this.connect();
        if (isAlive()) {
            for (String channel : subChannel) {
                this.addChannel(channel);
            }
        }
    }

    @Override
    public void onReceiveMsg(String message) {
        monitorThread.updateTimeoutInterval();
        if (message.contains("ping")) {
            this.sendPong(System.currentTimeMillis());
            return;
        }
        service.onReceive(message);
    }
}
