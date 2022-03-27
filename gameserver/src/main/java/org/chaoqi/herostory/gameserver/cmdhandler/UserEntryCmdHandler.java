package org.chaoqi.herostory.gameserver.cmdhandler;

import org.chaoqi.herostory.gameserver.Broadcaster;
import org.chaoqi.herostory.gameserver.model.User;
import org.chaoqi.herostory.gameserver.model.UserManager;
import org.chaoqi.herostory.gameserver.msg.GameMsgProtocol;

public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    @Override
    public void handle(MyCmdHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }

        int userId = ctx.getUserId();

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
