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
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        try {
            if (!(msg instanceof GeneratedMessageV3)) {
                super.write(ctx, msg, promise);
                return;
            }

            int msgCode = GameMsgRecognizer.getMsgCodeByClazz(msg.getClass());
            if (-1 == msgCode) {
                LOGGER.error(
                        "无法识别的消息类型， msgClazz = {}",
                        msg.getClass().getSimpleName()
                );

                super.write(ctx, msg, promise);
                return;
            }

            //消息体
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

            ByteBuf byteBuffer = ctx.alloc().buffer();
            //消息长度
            byteBuffer.writeShort((short)msgBody.length);
            //消息编码
            byteBuffer.writeShort((short)msgCode);
            //消息体
            byteBuffer.writeBytes(msgBody);

            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuffer);
            super.write(ctx, outputFrame, promise);
        } catch (Exception ex) {
            //处理错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
