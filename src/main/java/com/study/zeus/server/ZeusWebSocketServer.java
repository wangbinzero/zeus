package com.study.zeus.server;

import com.study.zeus.core.AbstractWebsocketServer;
import com.study.zeus.proto.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeusWebSocketServer extends AbstractWebsocketServer {

    private static final Logger logger = LoggerFactory.getLogger(ZeusWebSocketServer.class);



    @Override
    public void run() {

    }

    @Override
    public void onReceiveMessage(Request req, NioSocketChannel channel) {

    }

    @Override
    public void onLine(ChannelHandlerContext ctx) {

    }

    @Override
    public void offLine(ChannelHandlerContext ctx) {

    }
}
