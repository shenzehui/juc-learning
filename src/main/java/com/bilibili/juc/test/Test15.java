package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * 守护线程：其他非守护线程运行结束了，即使守护线程代码还没有执行完，也会强制结束
 * 垃圾回收器就是一种守护线程
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test15")
public class Test15 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            log.debug("结束");
        }, "t1");

        // 设置 t1 设置为守护线程
        t1.setDaemon(true);
        t1.start();

        Thread.sleep(1000);
        log.debug("结束");
    }
}
