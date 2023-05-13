package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * 偏向锁
 * Created by szh on 2023-05-13
 *
 * @author szh
 */
@Slf4j(topic = "c.TestBiased")
public class TestBiased {
    public static void main(String[] args) throws InterruptedException {
        // 偏向锁默认是有延迟的
        Dog d = new Dog();
        d.hashCode(); // 会禁用这个对象的偏向锁

        // 偏向锁、轻量级锁、
        log.debug(ClassLayout.parseInstance(d).toPrintable());

        // 加锁打印对象头
        synchronized (d) {
            log.debug(ClassLayout.parseInstance(d).toPrintable());
        }

        // 解锁后打印对象头
        log.debug(ClassLayout.parseInstance(d).toPrintable());
    }

}

class Dog {
}