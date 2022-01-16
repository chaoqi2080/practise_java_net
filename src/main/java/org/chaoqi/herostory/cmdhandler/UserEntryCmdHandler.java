package org.chaoqi.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.chaoqi.herostory.Broadcaster;
import org.chaoqi.herostory.model.MoveState;
import org.chaoqi.herostory.model.User;
import org.chaoqi.herostory.model.UserManager;
import org.chaoqi.herostory.msg.GameMsgProtocol;

public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }

        Integer userId = (Integer) ctx.attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }

        User user = UserManager.getByUserId(userId);
        if (null == user) {
            return;
        }

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(user.getUserId());
        resultBuilder.setUserName(user.getUserName());
        resultBuilder.setHeroAvatar(user.getUserAvatar());

        GameMsgProtocol.UserEntryResult result = resultBuilder.build();
        Broadcaster.broadcast(result);
    }
}
