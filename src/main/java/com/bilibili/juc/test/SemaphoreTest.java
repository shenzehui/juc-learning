package com.bilibili.juc.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Semaphore 信号量
 * Created by szh on 2023-06-01
 *
 * @author szh
 */

public class SemaphoreTest {

    public static final int THREAD_COUNT = 30;

    public static ExecutorService service = Executors.newFixedThreadPool(10);

    /**
     * 10 表示可用的许可证数量，也就是最大的并发数是 10
     */
    private static Semaphore s = new Semaphore(10);

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_COUNT; i++) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 获取许可证
                        s.acquire();
//                        s.tryAcquire()
                        System.out.println("save data");
                        // 释放许可证
                        s.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        service.shutdown();
    }
}
