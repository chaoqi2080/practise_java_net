package org.chaoqi.herostory.gatewayserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.attribute.UserPrincipalNotFoundException;

public class ClientMsgHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ClientMsgHandler.class);



    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            super.channelActive(ctx);

            Channel ch = ctx.channel();
            IdGetSetter.getInstance().setSessionId(ch);
            ClientChannelGroup.addChannel(ch);
        } catch (Exception ex) {
            //
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msgObj) throws Exception {
        LOGGER.info(
                ">>> 收到客户端消息 msgType = {} <<<",
                msgObj.getClass().getSimpleName()
        );

        int session_id = IdGetSetter.getInstance().getSessionId(ctx.channel());
        if (session_id <= 0) {
            LOGGER.error("获取 session_id 失败");
            return;
        }


        //需要做一个本地备份，离开当前 pipeline msg 自动销毁
        BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msgObj;
        ByteBuf oldBuf = inputFrame.content();
        //组合内部消息
        //len + session_id + code + content
        //[0, 0,              0, 13, 10, 1, 49, 18, 1, 49]
        //[0, 12, 0, 0, 0, 2, 0, 13, 10, 1, 49, 18, 1, 49]
        ByteBuf newBuf = ctx.alloc().buffer();

        //读取原来的消息长度
        oldBuf.readShort();

        //len
        newBuf.writeShort(oldBuf.readableBytes() + 4);
        newBuf.writeInt(session_id);
        oldBuf.readBytes(newBuf, oldBuf.readableBytes());

        LOGGER.info(
                "向游戏服务器发送消息 session_id = {}",
                session_id
        );

        BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(newBuf);
        NettyClient.getInstance().sendMsg(outputFrame);
    }
}
