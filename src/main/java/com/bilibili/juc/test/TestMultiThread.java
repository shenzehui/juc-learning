package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.TestMultiThread")
public class TestMultiThread {
    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                log.debug("running");
            }
        }, "t1").start();
        new Thread(() -> {
            while (true) {
                log.debug("running");
            }
        }, "t2").start();
    }
}
