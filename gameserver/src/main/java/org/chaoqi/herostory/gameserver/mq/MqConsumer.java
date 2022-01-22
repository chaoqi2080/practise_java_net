package org.chaoqi.herostory.gameserver.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.chaoqi.herostory.gameserver.conf.AllConf;
import org.chaoqi.herostory.gameserver.rank.RankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 消息队列消费者
 */
public final class MqConsumer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MqConsumer.class);

    /**
     * 私有化类构造器
     */
    private MqConsumer(){

    }

    /**
     * 初始化
     */
    static public void init() {
        if (!AllConf.ROCKETMQ_ENABLE) {
            LOGGER.warn("消息队列（ 生产者 ）跳过初始化");
            return;
        }

        //创建消息队列消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        //指定 nameServer 地址
        consumer.setNamesrvAddr(AllConf.RACKETMQ_NAMESRV_ADDR);

        try {
            //指定监听主题
            //必须保证主题存在，否则后面逻辑无法执行
            consumer.subscribe("hero_story_victor", "*");

            //注册毁掉
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgExtList, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                    for (Message msgExt : msgExtList) {
                        //获取战果消息
                        VictorMsg mqMsg = JSONObject.parseObject(
                                msgExt.getBody(),
                                VictorMsg.class
                        );

                        LOGGER.debug(
                                "从消息队列中收到战果，winnerId = {}, loserId = {}",
                                mqMsg.getWinnerId(),
                                mqMsg.getLoserId()
                        );

                        //刷新排名
                        RankService.getInstance().refreshRank(mqMsg.getWinnerId(), mqMsg.getLoserId());
                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            //启动消费者
            consumer.start();

            LOGGER.info("消息队列（ 消费者 ）连接成功");
        } catch (Exception ex) {
            //记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
