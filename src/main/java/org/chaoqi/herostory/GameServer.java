package org.chaoqi.herostory;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.chaoqi.herostory.cmdhandler.CmdHandlerFactory;
import org.chaoqi.herostory.conf.AllConf;
import org.chaoqi.herostory.mq.MqConsumer;
import org.chaoqi.herostory.mq.MqProducer;
import org.chaoqi.herostory.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);

    public static void main(String[] args) {
        // 设置 log4j 属性文件
        PropertyConfigurator.configure(GameServer.class.getClassLoader().getResourceAsStream("log4j.properties"));

        GameMsgRecognizer.init();   //初始化消息识别器
        CmdHandlerFactory.init();   //初始化命令处理器
        MySqlSessionFactory.init(); //初始化 MySql 会话工厂
        RedisUtil.init();           //初始化 Redis
        MqProducer.init();          //初始化消息队列

        //接收线程
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        //工作线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            //http 服务器编解码
                            new HttpServerCodec(),
                            //内容长度限制
                            new HttpObjectAggregator(65535),
                            //Websocket协议处理，处理握手、ping、pong 心跳等消息
                            new WebSocketServerProtocolHandler("/websocket"),
                            //转换成具体的消息
                            new GameMsgDecoder(),
                            //
                            new GameMsgEncoder(),
                            //处理消息
                            new GameMsgHandler()
                    );
                }
            });

            b.option(ChannelOption.SO_BACKLOG, 128);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            //监听端口
            ChannelFuture f = b.bind(AllConf.GAME_SERVER_PORT).sync();

            if (f.isSuccess()) {
                LOGGER.info("游戏服务器启动成功");
            }

            f.channel().closeFuture().sync();

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            workerGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }
}
