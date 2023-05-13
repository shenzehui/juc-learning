package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

/**
 * sleep 可以解决 CPU 占满 100% 情况
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test6")
public class Test6 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();

        log.debug("t1 state:{}", t1.getState()); // RUNNABLE

        Thread.sleep(500);

        log.debug("t1 state:{}", t1.getState()); // TIMED_WAITING
    }
}
