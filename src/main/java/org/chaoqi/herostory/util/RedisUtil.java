package org.chaoqi.herostory.util;

import org.chaoqi.herostory.conf.AllConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis 工具类
 */
public final class RedisUtil {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * Redis 连接池
     */
    static private JedisPool _jedisPool = null;

    /**
     * 私有化类默认构造器
     */
    private RedisUtil() {
    }

    /**
     * 初始化Redis连接池
     */
    static public void init() {
        try {
            _jedisPool = new JedisPool(AllConf.REDIS_SERVER_HOST, AllConf.REDIS_SERVER_PORT);
            LOGGER.info("Redis 连接成功!");
        } catch (Exception ex) {
            // 记录错误日志
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获取 Redis 实例
     *
     * @return Redis 对象
     */
    static public Jedis getRedis() {
        if (null == _jedisPool) {
            throw new RuntimeException("_jedisPool 尚未初始化");
        }

        Jedis redis = _jedisPool.getResource();
        //redis.auth("");

        return redis;
    }
}
