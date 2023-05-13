package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;

/**
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test11")
public class Test11 {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("sleep...");
            try {
                sleep(5000); // wait ,join
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.setName("t1");
        t1.start();
        Thread.sleep(1000);
        log.debug("interrupt");
        // 打断睡眠进程
        t1.interrupt();
        log.debug("打断标记:{}", t1.isInterrupted()); // 打断标记:false
    }
}
