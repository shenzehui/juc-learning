package com.bilibili.juc.n8;

import com.bilibili.juc.n2.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

/**
 * Semaphore:对访问资源个数的限制
 * Created by szh on 2023-05-20
 *
 * @author szh
 */

@Slf4j(topic = "c.TestSemaphore")
public class TestSemaphore {

    public static void main(String[] args) {
        // 1. 创建 semaphore 对象
        Semaphore semaphore = new Semaphore(3); // 许可、公平非公平

        // 2. 10个线程同时运行
        for (int i = 0; i < 19; i++) {
            new Thread(() -> {
                // 获得一个许可
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    log.debug("running...");
                    Sleeper.sleep(1);
                    log.debug("end...");
                } finally {
                    semaphore.release();
                }
            }).start();
        }
    }
}
