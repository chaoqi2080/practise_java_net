package org.chaoqi.herostory.gameserver.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * 命令处理器
 * @param <TCmd>
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    /**
     * 处理命令
     * @param ctx
     * @param msg
     */
    void handle(ChannelHandlerContext ctx, TCmd msg);
}
