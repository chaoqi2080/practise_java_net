package org.chaoqi.herostory.gatewayserver.conf;

/**
 * 所有配置
 */
public final class AllConf {
    /**
     * 网关服务器主机地址
     */
    static public final String GATE_SERVER_HOST = "0.0.0.0";
    /**
     *  网关服务器端口号
     */
    static public final int GATE_SERVER_PORT = 54321;

    /**
     * 游戏服务器主机地址
     */
    static public final String GAME_SERVER_HOST = "127.0.0.1";
    /**
     *  游戏服务器端口号
     */
    static public final int GAME_SERVER_PORT = 12345;


    /**
     * 私有化类默认构造器
     */
    private AllConf(){

    }
}
