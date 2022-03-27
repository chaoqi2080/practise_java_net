package org.chaoqi.herostory.gameserver;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (null == ctx || null == msg) {
            LOGGER.error("ctx or msg is empty");
            return;
        }

        try {
            if (!(msg instanceof InternalMessage)) {
                super.write(ctx, msg, promise);
                LOGGER.error("not internalMessage");
                return;
            }

            InternalMessage innerMsg = (InternalMessage) msg;
            GeneratedMessageV3 protoMsg = innerMsg.getMsg();

            int msgCode = GameMsgRecognizer.getMsgCodeByClazz(protoMsg.getClass());
            if (-1 == msgCode) {
                LOGGER.error(
                        "无法识别的消息类型， msgClazz = {}",
                        msg.getClass().getSimpleName()
                );

                super.write(ctx, msg, promise);
                return;
            }

            //消息体
            byte[] msgBody = protoMsg.toByteArray();

            ByteBuf byteBuf = ctx.alloc().buffer();
            byteBuf.writeShort((short)0);//占位
            byteBuf.writeInt(innerMsg.getRemoteSessionId());
            byteBuf.writeShort((short)msgCode);
            byteBuf.writeBytes(msgBody);
            //write message len.
            byteBuf.setShort(0, byteBuf.readableBytes()-2);

            LOGGER.info("回消息到客户端=>");
            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuf);
            super.write(ctx, outputFrame, promise);
        } catch (Exception ex) {
            //处理错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
