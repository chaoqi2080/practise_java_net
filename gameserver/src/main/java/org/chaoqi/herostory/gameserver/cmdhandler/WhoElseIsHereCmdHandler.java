package org.chaoqi.herostory.gameserver.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.chaoqi.herostory.gameserver.Broadcaster;
import org.chaoqi.herostory.gameserver.model.MoveState;
import org.chaoqi.herostory.gameserver.model.User;
import org.chaoqi.herostory.gameserver.model.UserManager;
import org.chaoqi.herostory.gameserver.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(WhoElseIsHereCmdHandler.class);

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

            MoveState mvState = currUser.getMoveState();

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder mvStateBuilder= GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvStateBuilder.setFromPosX(mvState.getFromX());
            mvStateBuilder.setFromPosY(mvState.getFromY());
            mvStateBuilder.setToPosX(mvState.getToX());
            mvStateBuilder.setToPosY(mvState.getToY());
            mvStateBuilder.setStartTime(mvState.getStartTime());
            userInfoBuilder.setMoveState(mvStateBuilder.build());

            resultBuilder.addUserInfo(userInfoBuilder.build());
        }

        GameMsgProtocol.WhoElseIsHereResult result = resultBuilder.build();
        LOGGER.info(result.toString());
        Broadcaster.broadcast(result);
    }
}
