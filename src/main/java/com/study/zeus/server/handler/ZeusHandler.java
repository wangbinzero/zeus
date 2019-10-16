package com.study.zeus.server.handler;

import com.alibaba.fastjson.JSON;
import com.study.zeus.proto.Request;
import com.study.zeus.proto.Response;
import com.study.zeus.server.ZeusWebSocketServer;
import com.study.zeus.service.KlineService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ZeusHandler extends SimpleChannelInboundHandler<Object> {


    private static final Logger logger = LoggerFactory.getLogger(ZeusHandler.class);
    private ZeusWebSocketServer zeusWebSocketServer;
    private KlineService klineService;

    public ZeusHandler(ZeusWebSocketServer zeusWebSocketServer, KlineService klineService) {
        this.zeusWebSocketServer = zeusWebSocketServer;
        this.klineService = klineService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof BinaryWebSocketFrame) {
            logger.info("binary frame");
        } else if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) msg;
            try {
                Request request = JSON.parseObject(textFrame.text(),Request.class);
                zeusWebSocketServer.onReceiveMessage(request, (NioSocketChannel) ctx.channel());
            } catch (Exception e) {
                e.printStackTrace();
                ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.paramError("参数错误",10001))));
            }
        }else if(frame instanceof CloseWebSocketFrame){
            logger.info("close connection");
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        zeusWebSocketServer.offLine(ctx);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            zeusWebSocketServer.onLine(ctx);
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
