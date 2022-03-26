package org.chaoqi.herostory.gatewayserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import org.chaoqi.herostory.gatewayserver.conf.AllConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    /**
     * 日志
     */
    static final private Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    /**
     * 单实例
     */
    static final private NettyClient _instance = new NettyClient();

    /**
     * 与游戏服务建立的连接
     */
    private Channel _gameServerChannel = null;

    /**
     * 私有化构造器
     */
    private NettyClient() {

    }

    public static NettyClient getInstance() {
        return _instance;
    }

    public void connect(String ip, int port) {
        try {
            WebSocketClientHandshaker handShake = WebSocketClientHandshakerFactory.newHandshaker(
                    new URI(
                            "ws://" + ip + ":" + port + "/websocket"
                    ),
                    WebSocketVersion.V13,
                    null,
                    true,
                    new DefaultHttpHeaders()
            );

            NioEventLoopGroup boosGroup = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(boosGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new HttpClientCodec(),
                            new HttpObjectAggregator(65535),
                            new WebSocketClientProtocolHandler(handShake)
                            //new InternalServerMsgHandler(ctx.channel())
                    );
                }
            });
            b.option(ChannelOption.SO_KEEPALIVE, true);

            //连接游戏服务器
            ChannelFuture f = b.connect(ip, port).sync();
            if (!f.isSuccess()) {
                LOGGER.error(">>> 连接游戏服务器失败 <<<");
                return;
            }

            LOGGER.info(">>> 连接游戏服务器成功 <<<");

            //等待 websocket 握手成功
            CountDownLatch countDownLatch = new CountDownLatch(8);
            while (!countDownLatch.await(200, TimeUnit.MILLISECONDS) &&
                    !handShake.isHandshakeComplete()) {
                countDownLatch.countDown();
            }

            if (!handShake.isHandshakeComplete()) {
                LOGGER.error("握手没有成功");
                return;
            }

            _gameServerChannel = f.channel();

            LOGGER.info(">>> 握手成功 <<<");
        }catch (Exception ex) {
            //
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public void sendMsg(Object msgObj) {
        if (null == _gameServerChannel) {
            LOGGER.error("_gameServerChannel 为空");
            return;
        }

        _gameServerChannel.writeAndFlush(msgObj);
    }
}
