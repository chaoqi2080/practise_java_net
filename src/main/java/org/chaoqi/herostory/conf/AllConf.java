package org.chaoqi.herostory.conf;

/**
 * 所有配置
 */
public final class AllConf {
    /**
     * 游戏服务器主机地址
     */
    static public final String GAME_SERVER_HOST = "127.0.0.1";
    /**
     *  游戏服务器端口号
     */
    static public final int GAME_SERVER_PORT = 12345;

    /**
     *  MySql 服务器地址
     */
    static public final String MYSQL_SERVER_HOST = "127.0.0.1";
    /**
     *  MySql 服务器端口号
     */
    static public final int MYSQL_SERVER_PORT = 3306;
    /**
     *  MySql 数据库
     */
    static public final String MYSQL_DB = "herostory";

    /**
     *  Redis 服务器主机地址
     */
    static public final String REDIS_SERVER_HOST = "127.0.0.1";

    /**
     *  Redis 服务器端口号
     */
    static public final int REDIS_SERVER_PORT = 6379;

    /**
     *  Redis 服务器密码
     */
    static public final String REDIS_PASSWORD = "";

    /**
     * 启动 RocketMQ
     */
    static public final boolean ROCKETMQ_ENABLE = true;

    /**
     * RocketMQ NameServer 地址
     */
    static public final String RACKETMQ_NAMESRV_ADDR = "127.0.0.1:9876";

    /**
     * 私有化类默认构造器
     */
    private AllConf(){

    }
}
