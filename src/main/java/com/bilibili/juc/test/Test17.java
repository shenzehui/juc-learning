package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

/**
 * 使用对象锁 synchronized 保证了临界区内代码的原子性
 * Created by szh on 2023-05-13
 *
 * @author szh
 */
class Room {
    private int counter = 0;

    /**
     * 锁的是对象  如果是静态方法，锁的就是类
     */
    public synchronized void increment() {
        // 临界区
        counter++;
    }

    public synchronized void decrement() {
        // 临界区
        counter--;
    }

    public synchronized int getCounter() {
        return counter;
    }
}

/**
 * @author szh
 */
@Slf4j(topic = "c.Test17")
public class Test17 {
    public static void main(String[] args) throws InterruptedException {
        Room room = new Room();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                room.increment();
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                room.decrement();
            }
        }, "t2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.debug("{}", room.getCounter());
    }
}