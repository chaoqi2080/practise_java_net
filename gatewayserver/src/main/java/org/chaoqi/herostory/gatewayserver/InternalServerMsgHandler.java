package org.chaoqi.herostory.gatewayserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalServerMsgHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(InternalServerMsgHandler.class);

    public InternalServerMsgHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msgObj) {
        if (null == ctx || null == msgObj) {
            LOGGER.error("ctx or msgObj null");
            return;
        }

        if (!(msgObj instanceof BinaryWebSocketFrame)) {
            LOGGER.error("不是能识别的 BinaryWebSocketFrame 消息");
            return;
        }

        BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msgObj;
        ByteBuf oldBuf = inputFrame.content();
        oldBuf.readShort();
        int sessionId = oldBuf.readInt();

        Channel ch = ClientChannelGroup.getChannelBySessionId(sessionId);
        if (ch == null) {
            LOGGER.error("找不到 sessionId = {} 对应的 channel", sessionId);
            return;
        }

        ByteBuf newBuf = ctx.alloc().buffer();
        newBuf.writeShort(0);
        newBuf.writeBytes(oldBuf);
        newBuf.setShort(0, oldBuf.readableBytes());

        LOGGER.info("回消息到客户端=>");

        BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(newBuf);
        ch.writeAndFlush(outputFrame);
    }
}
