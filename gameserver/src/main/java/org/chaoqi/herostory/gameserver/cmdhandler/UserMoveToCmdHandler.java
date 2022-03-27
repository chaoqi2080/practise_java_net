package org.chaoqi.herostory.gameserver.cmdhandler;

import org.chaoqi.herostory.gameserver.Broadcaster;
import org.chaoqi.herostory.gameserver.model.MoveState;
import org.chaoqi.herostory.gameserver.model.User;
import org.chaoqi.herostory.gameserver.model.UserManager;
import org.chaoqi.herostory.gameserver.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(UserMoveToCmdHandler.class);

    @Override
    public void handle(MyCmdHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }

        //获取跟当前 ctx 绑定的用户id
        int userId = ctx.getUserId();

        // 获取存在的用户
        User existUser = UserManager.getByUserId(userId);
        if (null == existUser) {
            return;
        }

        //提前保存时间作为移动开始时间
        long nowTime = System.currentTimeMillis();

        //保存玩家移动状态
        MoveState mvState = new MoveState();
        mvState.setFromX(cmd.getMoveFromPosX());
        mvState.setFromY(cmd.getMoveFromPosY());
        mvState.setToX(cmd.getMoveToPosX());
        mvState.setToY(cmd.getMoveToPosY());
        mvState.setStartTime(nowTime);
        existUser.setMoveState(mvState);

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(cmd.getMoveFromPosX());
        resultBuilder.setMoveFromPosY(cmd.getMoveFromPosY());
        resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
        resultBuilder.setMoveToPosY(cmd.getMoveToPosY());
        resultBuilder.setMoveStartTime(nowTime);

        GameMsgProtocol.UserMoveToResult result = resultBuilder.build();
        LOGGER.info(result.toString());
        Broadcaster.broadcast(result);
    }
}
