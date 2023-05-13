package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test8")
public class Test8 {
    public static void main(String[] args) throws InterruptedException {
        log.debug("enter");
        // 可读性更好的 sleep 方法
        TimeUnit.SECONDS.sleep(1);
        log.debug("end");
    }
}
