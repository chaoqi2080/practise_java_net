package org.chaoqi.herostory;

import org.chaoqi.herostory.mq.MqConsumer;
import org.chaoqi.herostory.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 排行榜统计程序
 */
public class RankApp {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RankApp.class);

    public static void main(String[] args) {
        RedisUtil.init();   //初始化 Redis
        MqConsumer.init();  //初始化消息队列

        LOGGER.info(">>> 排行榜维护进程启动成功！<<<");
    }
}
