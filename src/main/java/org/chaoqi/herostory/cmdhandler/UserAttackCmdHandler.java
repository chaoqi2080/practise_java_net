package org.chaoqi.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.chaoqi.herostory.msg.GameMsgProtocol;

public class UserAttackCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd msg) {

    }
}
