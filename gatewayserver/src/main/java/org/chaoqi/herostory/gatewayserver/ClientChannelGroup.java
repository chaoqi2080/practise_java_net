package org.chaoqi.herostory.gatewayserver;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientChannelGroup {
    /**
     * 信道组，为了实现群发
     */
    static private final Map<Integer, Channel> _chMap = new ConcurrentHashMap<>();

    /**
     * 私有化构造器
     */
    private ClientChannelGroup() {

    }

    /**
     *增加信道
     * @param ch
     */
    static public void addChannel(Channel ch) {
        if (null == ch) {
            return;
        }

        _chMap.put(IdGetSetter.getInstance().getSessionId(ch), ch);
    }

    /**
     * 移除信道
     * @param ch
     */
    static public void removeChannel(Channel ch) {
        if (null == ch) {
            return;
        }

        _chMap.values().remove(ch);
    }

    /**
     * 广播消息
     * @param msg
     */
    static public void broadcast(Object msg) {
        if (null == msg) {
            return;
        }

        _chMap.values().forEach((ch)-> {
            ch.writeAndFlush(msg);
        });
    }

    static public Channel getChannelBySessionId(int sessionId) {
        return _chMap.get(sessionId);
    }
}
