package org.chaoqi.herostory.async;

import org.chaoqi.herostory.MainMsgProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncOperationProcessor {
    /**
     * 日志
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    /**
     * 单例
     */
    static private AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    static private ExecutorService _es = Executors.newSingleThreadExecutor((runnable)->{
        //给此消息处理器一个名称，方便跟踪
        Thread thread = new Thread(runnable);
        thread.setName("AsyncOperationProcessor");
        return thread;
    });

    /**
     * 私有化构造器
     */
    private AsyncOperationProcessor(){}

    /**
     * 获取消息处理器
     * @return
     */
    static public AsyncOperationProcessor getInstance() {
        return _instance;
    }

    /**
     * 消息处理
     * @param op
     */
    public void process(IAsyncOperation op) {
        if (null == op) {
            return;
        }

        _es.submit(()-> {
            //异步执行
            op.doAsync();

            //把处理完成的结果，丢到消息处理线程执行
            MainMsgProcessor.getInstance().process(() -> op.doFinish());
        });
    }
}
