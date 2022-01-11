package org.chaoqi.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 消息广播器
 *
 */
public final class Broadcaster {
    /**
     * 信道组，为了实现群发
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化构造器
     */
    private Broadcaster() {}

    /**
     *增加信道
     * @param ch
     */
    static public void addChannel(Channel ch) {
        if (null == ch) {
            return;
        }

        _channelGroup.add(ch);
    }

    /**
     * 移除信道
     * @param ch
     */
    static public void removeChannel(Channel ch) {
        if (null == ch) {
            return;
        }

        _channelGroup.remove(ch);
    }

    /**
     * 广播消息
     * @param msg
     */
    static public void broadcast(Object msg) {
        if (null == msg) {
            return;
        }

        _channelGroup.writeAndFlush(msg);
    }

}
