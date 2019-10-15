package com.study.zeus.huobi;

import com.study.zeus.core.AbstractWebsocketClient;
import com.study.zeus.huobi.service.HuobiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.UUID;

public class HuobiClient extends AbstractWebsocketClient {

    private static final Logger logger = LoggerFactory.getLogger(HuobiClient.class);
    private String url;
    private HuobiService service;
    private String subId="";

    public HuobiClient(String url, HuobiService service) {
        this.url = url;
        this.service = service;
    }

    @Override
    public void connect() {
        try{
            subId= UUID.randomUUID().toString();
            final URI uri = URI.create(url);


        }catch (Exception e){
            logger.error("火币客户端启动出错: [{}]",e.getMessage());
            e.printStackTrace();
            if(group!=null){
                group.shutdownGracefully();
            }
        }
    }

    @Override
    public void sendPing() {

    }

    @Override
    public void addChannel(String channel) {

    }

    @Override
    public void removeChannel(String channel) {

    }

    @Override
    public void reConnect() {

    }

    @Override
    public void onReceiveMsg(String message) {

    }
}
