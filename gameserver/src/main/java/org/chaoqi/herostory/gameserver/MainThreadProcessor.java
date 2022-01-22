package org.chaoqi.herostory.gameserver;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.chaoqi.herostory.gameserver.cmdhandler.CmdHandlerFactory;
import org.chaoqi.herostory.gameserver.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单线程消息处理器
 */
public final class MainThreadProcessor {
    /**
     * 日志
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);

    /**
     * 单例
     */
    static private MainThreadProcessor _instance = new MainThreadProcessor();

    /**
     * 处理线程
     */
    static private ExecutorService _es = Executors.newSingleThreadExecutor((runnable)->{
        //给此消息处理器一个名称，方便跟踪
        Thread thread = new Thread(runnable);
        thread.setName("MainMsgProcessor");
        return thread;
    });

    /**
     * 私有化构造器
     */
    private MainThreadProcessor(){}

    /**
     * 获取消息处理器
     * @return
     */
    static public MainThreadProcessor getInstance() {
        return _instance;
    }

    /**
     * 消息处理
     * @param ctx
     * @param msg
     */
    public void process(ChannelHandlerContext ctx, Object msg) {

        LOGGER.info(
                "收到客户端消息, clazz = {}, msgBody = {}",
                msg.getClass().getSimpleName(),
                msg
        );

        _es.submit(()->{
            try {
                ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());
                if (null != cmdHandler) {
                    cmdHandler.handle(ctx, cast(msg));
                }
            } catch (Exception ex) {
                //
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }

    /**
     * 执行 Runnable
     * @param runnable
     */
    public void process(Runnable runnable) {
        if (null == runnable) {
            return;
        }

        _es.submit(new SafeRun(runnable));
    }


    /**
     * 转型为命令对象
     * @param msg
     * @param <TCmd>
     * @return
     */
    private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (null != msg && msg instanceof GeneratedMessageV3) {
            return (TCmd) msg;
        } else {
            return null;
        }
    }

    /**
     * 安全运行
     */
    static private class SafeRun implements Runnable {
        /**
         * 内置运行实例
         */
        private Runnable _innerR;

        /**
         * 参数构造器
         * @param _innerR
         */
        public SafeRun(Runnable _innerR) {
            this._innerR = _innerR;
        }

        @Override
        public void run() {
            if (null == _innerR) {
                return;
            }

            try {
                //运行
                _innerR.run();
            } catch (Exception ex) {
                //记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
