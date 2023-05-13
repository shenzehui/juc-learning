package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test14")
public class Test14 {
    private static void test4() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                log.debug("park...");
                LockSupport.park();
                log.debug("打断状态：{}", Thread.interrupted());
            }
        });
        t1.start();


        sleep(1);
        t1.interrupt();
    }

    private static void test3() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("park...");
            LockSupport.park();
            log.debug("unpark...");
//            log.debug("打断状态：{}", Thread.currentThread().isInterrupted());
            // interrupted 会重新设置打断标记为 false，可以使  LockSupport.park() 行代码生效
            log.debug("打断状态：{}", Thread.interrupted());

            // 这里会失效
            LockSupport.park();
            log.debug("unpark....");
        }, "t1");
        t1.start();

        sleep(1);

        // 打断正在处于 park 中的进程，打断后就可以继续运行了
        t1.interrupt();

    }

    public static void main(String[] args) throws InterruptedException {
        test3();
    }
}
