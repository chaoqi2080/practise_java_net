package org.chaoqi.herostory.gameserver.cmdhandler;

import org.chaoqi.herostory.gameserver.Broadcaster;
import org.chaoqi.herostory.gameserver.model.User;
import org.chaoqi.herostory.gameserver.model.UserManager;
import org.chaoqi.herostory.gameserver.mq.MqProducer;
import org.chaoqi.herostory.gameserver.mq.VictorMsg;
import org.chaoqi.herostory.gameserver.msg.GameMsgProtocol;

public class UserAttackCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    @Override
    public void handle(MyCmdHandlerContext ctx, GameMsgProtocol.UserAttkCmd msg) {
        if (null == ctx || null == msg) {
            return;
        }

        int userId = ctx.getUserId();

        User user = UserManager.getByUserId(userId);
        if (null == user) {
            return;
        }

        User targetUser = UserManager.getByUserId(msg.getTargetUserId());
        if (null == targetUser) {
            broadcastAttackResult(userId, -1);
            return;
        }

        final int dmgPoint = 10;

        targetUser.setCurHp(targetUser.getCurHp() - dmgPoint);

        //广播攻击结果
        broadcastAttackResult(userId, msg.getTargetUserId());
        //广播减血结果
        broadcastSubstractHp(msg.getTargetUserId(), dmgPoint);
        //广播死亡结果
        if (targetUser.getCurHp() <= 0) {
            broadcastUserDieResult(msg.getTargetUserId());

            VictorMsg mqMsg = new VictorMsg();
            mqMsg.setWinnerId(userId);
            mqMsg.setLoserId(targetUser.getUserId());
            MqProducer.sendMsg("hero_story_victor", mqMsg);
        }
    }

    /**
     * 广播用户被攻击
     * @param attkUserId
     * @param targetUserId
     */
    static private void broadcastAttackResult(int attkUserId, int targetUserId) {
        if (attkUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult result = resultBuilder.build();
        Broadcaster.broadcast(result);
    }

    /**
     * 广播用户减血
     * @param targetUserId
     * @param substractHp
     */
    static private void broadcastSubstractHp(int targetUserId, int substractHp) {
        if (targetUserId <= 0 || substractHp <= 0) {
            return;
        }

        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(substractHp);

        GameMsgProtocol.UserSubtractHpResult result = resultBuilder.build();
        Broadcaster.broadcast(result);
    }

    /**
     * 广播用户死亡信息
     * @param targetUserId
     */
    static private void broadcastUserDieResult(int targetUserId) {
        if (targetUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult result = resultBuilder.build();
        Broadcaster.broadcast(result);
    }
}
