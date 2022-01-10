package org.chaoqi.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.chaoqi.herostory.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 日志
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);
    /**
     * 信道组，为了实现群发
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }

        try {
            super.channelActive(ctx);
            //把当前 channel 添加进信道组
            _channelGroup.add(ctx.channel());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        LOGGER.info(
                "收到客户端消息, clazz = {}, msgBody = {}",
                msg.getClass().getSimpleName(),
                msg
        );

        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            int userId = cmd.getUserId();
            String avatar = cmd.getHeroAvatar();

            GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
            resultBuilder.setUserId(userId);
            resultBuilder.setHeroAvatar(avatar);

            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
            _channelGroup.writeAndFlush(newResult);
        }
    }
}
