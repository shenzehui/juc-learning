package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test1")
public class Test1 {

    public static void main(String[] args) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                log.debug("running");
            }
        };
        thread.setName("t1");
        thread.start();
        log.debug("running");
    }
}
