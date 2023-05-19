package com.bilibili.juc.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * newFixedThreadPool 固定大小的线程池
 * Created by szh on 2023-05-18
 *
 * @author szh
 */

@Slf4j(topic = "c.TestThreadPoolExecutors")
public class TestThreadPoolExecutors {

    public static void main(String[] args) {
        // 固定大小的线程池
        ExecutorService pool = Executors.newFixedThreadPool(2, new ThreadFactory() {
            // 这里影响的是线程的名称
            private AtomicInteger t = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                // 线程名称
                return new Thread(r, "mypool_t" + t.getAndIncrement());
            }
        });
        pool.execute(() -> {
            log.debug("1");
        });
        pool.execute(() -> {
            log.debug("2");
        });
        pool.execute(() -> {
            log.debug("3");
        });
        // 12:03:00.035 [pool-1-thread-1] DEBUG c.TestThreadPoolExecutors - 1
        // 12:03:00.035 [pool-1-thread-2] DEBUG c.TestThreadPoolExecutors - 2
        // 12:03:00.037 [pool-1-thread-1] DEBUG c.TestThreadPoolExecutors - 3
    }
}
