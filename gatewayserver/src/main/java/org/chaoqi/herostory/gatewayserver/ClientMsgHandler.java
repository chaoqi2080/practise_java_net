package org.chaoqi.herostory.gatewayserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
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

public class ClientMsgHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ClientMsgHandler.class);

    private Channel _gameServerChannel = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            super.channelActive(ctx);

            WebSocketClientHandshaker handShake = WebSocketClientHandshakerFactory.newHandshaker(
                    new URI(
                            "ws://" + AllConf.GAME_SERVER_HOST + ":" + AllConf.GAME_SERVER_PORT + "/websocket"
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
                            new WebSocketClientProtocolHandler(handShake),
                            new InternalServerMsgHandler(ctx.channel())
                    );
                }
            });
            b.option(ChannelOption.SO_KEEPALIVE, true);

            //连接游戏服务器
            ChannelFuture f = b.connect(AllConf.GAME_SERVER_HOST, AllConf.GAME_SERVER_PORT).sync();
            if (!f.isSuccess()) {
                LOGGER.error(">>> 连接游戏服务器失败 <<<");
                return;
            }

            LOGGER.info(">>> 连接游戏服务器成功 <<<");

            //等待 websocket 握手成功
            CountDownLatch countDownLatch = new CountDownLatch(8);
            while (!countDownLatch.await(200, TimeUnit.MILLISECONDS) && !handShake.isHandshakeComplete()) {
                countDownLatch.countDown();
            }

            if (!handShake.isHandshakeComplete()) {
                LOGGER.error("握手没有成功");
                return;
            }

            LOGGER.info(">>> 握手成功 <<<");

            _gameServerChannel = f.channel();
        } catch (Exception ex) {
            //
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (null != _gameServerChannel) {
            _gameServerChannel.close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info(
                ">>> 收到客户端消息 msgType = {} <<<",
                msg.getClass().getSimpleName()
        );

        if (null == _gameServerChannel) {
            LOGGER.error(">>> 没有连接上游戏服务端 <<<");
            return;
        }

        //需要做一个本地备份，离开当前 pipeline msg 自动销毁
        BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
        BinaryWebSocketFrame outputFrame = inputFrame.copy();
        _gameServerChannel.writeAndFlush(outputFrame);
    }
}
