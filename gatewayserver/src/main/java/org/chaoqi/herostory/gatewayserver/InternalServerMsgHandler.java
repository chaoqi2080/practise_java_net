package org.chaoqi.herostory.gatewayserver;

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

    private Channel _clientChannel = null;

    public InternalServerMsgHandler(Channel ch) {
        _clientChannel = ch;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        if (null == _clientChannel) {
            //
            LOGGER.error("_gameChannel is empty.");
            return;
        }

        BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
        BinaryWebSocketFrame outputFrame = inputFrame.copy();
        _clientChannel.writeAndFlush(outputFrame);
    }
}
