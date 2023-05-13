package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test5")
public class Test5 {
    public static void main(String[] args) {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("running....");
            }
        };
        // getState() 获取线程状态
        System.out.println(t1.getState());  // NEW
        t1.start();
        System.out.println(t1.getState());  // RUNNABLE
    }
}
