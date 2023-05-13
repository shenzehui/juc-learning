package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;

/**
 * 打断正常运行线程
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test12")
public class Test12 {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                boolean interrupted = Thread.currentThread().isInterrupted();
                // 通过打断标记来优雅的停止线程
                if (interrupted) {
                    log.debug("被打断了，退出循环");
                    break;
                }
            }
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        log.debug("interrupt");
        // 打断睡眠进程
        t1.interrupt();
        log.debug("打断标记:{}", t1.isInterrupted()); // 打断标记:true
    }
}
