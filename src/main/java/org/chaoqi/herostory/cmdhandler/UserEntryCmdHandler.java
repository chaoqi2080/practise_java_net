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

        int userId = cmd.getUserId();
        String avatar = cmd.getHeroAvatar();

        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setUserAvatar(avatar);
        newUser.setCurHp(100);
        newUser.setMoveState(new MoveState());
        UserManager.addUser(newUser);

        //把当前用户id 绑定到 ctx
        ctx.attr (AttributeKey.valueOf("userId")).set(userId);

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(avatar);

        GameMsgProtocol.UserEntryResult result = resultBuilder.build();
        Broadcaster.broadcast(result);
    }
}
