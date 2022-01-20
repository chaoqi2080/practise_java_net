package org.chaoqi.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.chaoqi.herostory.conf.AllConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息队列生产者
 */
public final class MqProducer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MqProducer.class);

    /**
     * 私有化类构造器
     */
    private MqProducer(){

    }

    /**
     * 消息队列生产者
     */
    static private DefaultMQProducer _producer = null;

    /**
     * 初始化
     */
    static public void init() {
        if (!AllConf.ROCKETMQ_ENABLE) {
            LOGGER.warn("消息队列（ 生产者 ）跳过初始化！");
            return;
        }
        try {
            //创建消息队列生产者
            DefaultMQProducer producer = new DefaultMQProducer("hero_story");
            //指定 nameServer 地址
            producer.setNamesrvAddr(AllConf.RACKETMQ_NAMESRV_ADDR);
            //启动生产者
            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);

            _producer = producer;

            LOGGER.info("消息队列 ( 生产者 ) 连接成功!");
        } catch (Exception ex) {
            //
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 发送消息
     * @param topic 主题
     * @param msg 消息对象
     */
    static public void sendMsg(String topic, Object msg) {
        if (null == topic || null == msg) {
            return;
        }

        if (null == _producer) {
            if (AllConf.ROCKETMQ_ENABLE) {
                LOGGER.error("_producer 尚未初始化");
            }

            return;
        }

        Message newMsg = new Message();
        newMsg.setTopic(topic);
        newMsg.setBody(JSONObject.toJSONBytes(msg));

        try {
            _producer.send(newMsg);
        } catch (Exception ex) {
            //记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }


}
