package org.chaoqi.herostory.gatewayserver;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGetSetter {
    /**
     * id 生成器
     */
    private static AtomicInteger _idGen = new AtomicInteger(0);

    /**
     * 私有化构造器
     */
    private IdGetSetter() {

    }

    /**
     * 单实例
     */
    static private IdGetSetter _instance = new IdGetSetter();

    /**
     * 获取单实例
     * @return
     */
    static public IdGetSetter getInstance() {
        return _instance;
    }

    /**
     * 给channel 设置 session_id
     * @param channel 被设置 session_id 的 channel
     */
    public void setSessionId(Channel channel) {
        if (null == channel) {
            return;
        }

        channel.attr(AttributeKey.valueOf("session_id")).set(_idGen.incrementAndGet());
    }

    /**
     * 获取channel 的 session_id
     * @param channel 想获取 session_id 的 channel
     * @return
     */
    public Integer getSessionId(Channel channel) {
        if (null == channel) {
            return -1;
        }

        Object objVal = channel.attr(AttributeKey.valueOf("session_id")).get();
        if (objVal instanceof Integer) {
            return (Integer) objVal;
        }

        return -1;
    }


}
