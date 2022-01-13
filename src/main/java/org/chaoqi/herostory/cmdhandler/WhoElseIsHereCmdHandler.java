package org.chaoqi.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.chaoqi.herostory.Broadcaster;
import org.chaoqi.herostory.User;
import org.chaoqi.herostory.UserManager;
import org.chaoqi.herostory.msg.GameMsgProtocol;

import java.util.Collection;

public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }

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
    }
}
