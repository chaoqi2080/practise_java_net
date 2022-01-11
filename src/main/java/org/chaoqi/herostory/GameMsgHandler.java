package org.chaoqi.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.chaoqi.herostory.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

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

            Integer userId = (Integer) ctx.attr(AttributeKey.valueOf("userId")).get();
            if (null == userId) {
                return;
            }

            //移除当前用户
            UserManager.removeByUserId(userId);
            //移除自己的 channel
            Broadcaster.removeChannel(ctx.channel());

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

            User newUser = new User();
            newUser.setUserId(userId);
            newUser.setUserAvatar(avatar);
            UserManager.addUser(newUser);

            //把当前用户id 绑定到 ctx
            ctx.attr (AttributeKey.valueOf("userId")).set(userId);

            GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
            resultBuilder.setUserId(userId);
            resultBuilder.setHeroAvatar(avatar);

            GameMsgProtocol.UserEntryResult result = resultBuilder.build();
            Broadcaster.broadcast(result);
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

            Collection<User> userList = UserManager.listUser();
            for (User currUser : userList) {
                if (null == currUser) {
                    continue;
                }

                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfoBuilder.setUserId(currUser.getUserId());
                userInfoBuilder.setHeroAvatar(currUser.getUserAvatar());

                resultBuilder.addUserInfo(userInfoBuilder.build());
            }

            GameMsgProtocol.WhoElseIsHereResult result = resultBuilder.build();
            Broadcaster.broadcast(result);
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg;

            //获取跟当前 ctx 绑定的用户id
            Integer userId = (Integer) ctx.attr(AttributeKey.valueOf("userId")).get();
            if (null == userId) {
                return;
            }

            GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
            resultBuilder.setMoveUserId(userId);
            resultBuilder.setMoveFromPosX(cmd.getMoveFromPosX());
            resultBuilder.setMoveFromPosY(cmd.getMoveFromPosY());

            GameMsgProtocol.UserMoveToResult result = resultBuilder.build();
            Broadcaster.broadcast(result);
        }
    }
}
