package org.chaoqi.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.chaoqi.herostory.Broadcaster;
import org.chaoqi.herostory.GameMsgEncoder;
import org.chaoqi.herostory.model.User;
import org.chaoqi.herostory.model.UserManager;
import org.chaoqi.herostory.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.LinkOption;
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

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder mvStateBuilder= GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvStateBuilder.setFromPosX(currUser.moveState.getFromX());
            mvStateBuilder.setFromPosY(currUser.moveState.getFromY());
            mvStateBuilder.setToPosX(currUser.moveState.getToX());
            mvStateBuilder.setToPosY(currUser.moveState.getToY());
            mvStateBuilder.setStartTime(currUser.moveState.getStartTime());
            userInfoBuilder.setMoveState(mvStateBuilder.build());

            resultBuilder.addUserInfo(userInfoBuilder.build());
        }

        GameMsgProtocol.WhoElseIsHereResult result = resultBuilder.build();
        LOGGER.info(result.toString());
        Broadcaster.broadcast(result);
    }
}
