package org.chaoqi.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.chaoqi.herostory.Broadcaster;
import org.chaoqi.herostory.msg.GameMsgProtocol;

public class UserMoveToCmdHandler {
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
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
