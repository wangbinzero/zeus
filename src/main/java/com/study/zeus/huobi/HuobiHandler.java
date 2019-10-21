package com.study.zeus.huobi;

import com.study.zeus.utils.GzipUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuobiHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(HuobiHandler.class);
    private WebSocketClientHandshaker handshaker;
    private ChannelPromise promise;
    private HuobiClient client;

    public HuobiHandler(WebSocketClientHandshaker handshaker, HuobiClient client) {
        this.handshaker = handshaker;
        this.client = client;
    }

    public ChannelFuture channelPromise() {
        return promise;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(channel, (FullHttpResponse) msg);
            promise.setSuccess();
            return;
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof BinaryWebSocketFrame) {
            String message = GzipUtil.decodeByteBuf(frame.content());
            client.onReceiveMsg(message);

        } else if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            client.onReceiveMsg(textFrame.text());

        } else if (frame instanceof PongWebSocketFrame) {
            logger.info("接收到火币心跳数据包");
        } else if (frame instanceof CloseWebSocketFrame) {
            logger.info("接收到火币关闭数据包");
            channel.close();
        }
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        promise = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        handshaker.handshake(ctx.channel());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        cause.printStackTrace();
        logger.error("异常信息: [{}]", cause.getMessage());

        if (!promise.isDone()) {
            promise.setFailure(cause);
        }
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.error("火币客户端断开连接: [{}]", client.isAlive());
        client.reConnect();
        logger.info("重连结果: [{}]", client.isAlive());
    }
}
