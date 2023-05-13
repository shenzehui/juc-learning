package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test16")
public class Test16 {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("洗水壶");
            sleep(1);
            log.debug("烧开水");
            sleep(5);
        }, "老王");


        Thread t2 = new Thread(() -> {
            log.debug("洗茶壶");
            sleep(1);
            log.debug("洗茶杯");
            sleep(2);
            log.debug("拿茶叶");
            sleep(1);
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("泡茶");
        }, "小王");

        t1.start();
        t2.start();
    }
}


