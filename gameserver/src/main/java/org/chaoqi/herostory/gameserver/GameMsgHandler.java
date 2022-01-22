package org.chaoqi.herostory.gameserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.chaoqi.herostory.gameserver.model.UserManager;
import org.chaoqi.herostory.gameserver.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 日志
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }

        try {
            super.channelActive(ctx);

            //把当前 channel 添加进信道组
            Broadcaster.addChannel(ctx.channel());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            super.handlerRemoved(ctx);

            //移除自己的 channel
            Broadcaster.removeChannel(ctx.channel());

            Integer userId = (Integer) ctx.attr(AttributeKey.valueOf("userId")).get();
            if (null == userId) {
                return;
            }

            //移除当前用户
            UserManager.removeByUserId(userId);

            GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
            resultBuilder.setQuitUserId(userId);

            //广播用户退出
            GameMsgProtocol.UserQuitResult result = resultBuilder.build();
            Broadcaster.broadcast(result);
        } catch (Exception ex) {
            //记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (null == ctx || null == msg) {
            return;
        }

        MainThreadProcessor.getInstance().process(ctx, msg);
    }
}
