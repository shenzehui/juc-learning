package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test2")
public class Test2 {

//    public static void main(String[] args) {
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                log.debug("running");
//            }
//        };
//        Thread t2 = new Thread(r, "t2");
//        t2.start();
//        log.debug("running");
//    }

    public static void main(String[] args) {
        // 精简写法
        Runnable r = () -> log.debug("running");
        Thread t2 = new Thread(r, "t2");
        t2.start();
        log.debug("running");
    }
}
