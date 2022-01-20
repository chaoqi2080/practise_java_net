package org.chaoqi.herostory.async;

/**
 * 异步操作接口
 */
public interface IAsyncOperation {
    /**
     * 执行异步操作
     */
    void doAsync();

    /**
     * 执行完成逻辑
     */
    default void doFinish() {

    }

    /**
     * 绑定id (把同一用户的同样操作锁定到同一个线程，避免多线程问题)
     * @return
     */
    default int getBindId() {
        return 0;
    }
}
