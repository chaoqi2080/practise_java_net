package org.chaoqi.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.chaoqi.herostory.msg.GameMsgProtocol;
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

        try {
            BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = inputFrame.content();

            //读取消息长度，netty 解决了粘包的问题，不用此数据
            byteBuf.readShort();
            //读取消息序号
            int msgCode = byteBuf.readShort();

            //读取消息体
            byte[] msgBody = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgBody);

            GeneratedMessageV3 cmd = null;

            switch (msgCode) {
                case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                    cmd = GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
                    break;

                default:
                    break;
            }

            if (null != cmd) {
                ctx.fireChannelRead(cmd);
            }
        } catch (Exception ex) {
            //记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

    }
}
