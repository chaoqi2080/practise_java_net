package org.chaoqi.herostory.gameserver;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }


        BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
        ByteBuf byteBuf = inputFrame.content();

        byteBuf.readShort();
        int remoteSessionId = byteBuf.readInt();
        int msgCode = byteBuf.readShort();

        //读取消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);

        LOGGER.info(
                "remoteSessionId = {}, msgBody = {}",
                remoteSessionId,
                msgBody
        );


        //获取消息处理器
        Message.Builder defaultBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
        if (null == defaultBuilder) {
            LOGGER.error(
                    "遗漏了未处理的消息 = {}",
                    msgCode
            );
            return;
        }
        defaultBuilder.clear();
        defaultBuilder.mergeFrom(msgBody);

        Message cmd = defaultBuilder.build();

        if (null != cmd) {
            LOGGER.info(
                    "处理消息 {}",
                    cmd.getClass().getSimpleName()
            );

            InternalMessage innerMsg = new InternalMessage();
            innerMsg.setMsgObj((GeneratedMessageV3) cmd);
            innerMsg.setRemoteSessionId(remoteSessionId);

            ctx.fireChannelRead(innerMsg);
        }

    }
}
