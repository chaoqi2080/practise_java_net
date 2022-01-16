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
    static private final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    /**
     * 线程数组
     */
    private final ExecutorService[] _esArray = new ExecutorService[8];

    /**
     * 私有化构造器
     */
    private AsyncOperationProcessor(){
        for (int i = 0; i < _esArray.length; i++) {
            final String processName = "AsyncOperationProcessor-" + i;
            _esArray[i] = Executors.newSingleThreadExecutor((runnable)->{
                Thread thread = new Thread(runnable);
                thread.setName(processName);
                return thread;
            });
        }
    }

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

        int bindId = Math.abs(op.getBindId());
        int esIndex = bindId % _esArray.length;

        _esArray[esIndex].submit(()-> {
            //异步执行
            op.doAsync();

            //把处理完成的结果，丢到消息处理线程执行
            MainMsgProcessor.getInstance().process(() -> op.doFinish());
        });
    }
}
