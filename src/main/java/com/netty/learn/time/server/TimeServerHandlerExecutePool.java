package com.netty.learn.time.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhang Anjin
 * @description TimeServer Execute Pool
 * @date 2023/10/18 07:02
 */
public class TimeServerHandlerExecutePool {
    private ExecutorService executorService;

    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
        executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                maxPoolSize, 120L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueSize));
    }

    /**
     * execute task
     *
     * @param timeServerHandler .
     */
    public void execute(TimeServerHandler timeServerHandler) {
        executorService.execute(timeServerHandler);
    }
}
