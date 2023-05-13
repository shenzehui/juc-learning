package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test7")
public class Test7 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("enter sleep...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.debug("wake up...");
                    e.printStackTrace();
                }
            }
        };
        t1.start();

        // 让主线程睡眠一秒
        Thread.sleep(1000);

        log.debug("interrupt....");

        // 唤醒 t1 线程，会抛出异常
        t1.interrupt();
    }
}
