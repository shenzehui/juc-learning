package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * wait notify 解决固定运行顺序问题
 * Created by szh on 2023-05-14
 *
 * @author szh
 */

@Slf4j(topic = "c.Test25")
public class Test25 {
    static final Object lock = new Object();

    /**
     * 表示 t2 是否运行过
     */
    static boolean t2runned = false;

    private static void method1() {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                while (!t2runned) { // 加 while 循环方式虚假唤醒
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.debug("1");
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                // 先执行 t2 线程，会空唤醒，但是不影响 t1 线程执行
                log.debug("2");
                t2runned = true;
                lock.notify();
            }
        }, "t2");

        t1.start();

        t2.start();

    }

    private static void method2() {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            log.debug("1");
        }, "t1");

        t1.start();

        Thread t2 = new Thread(() -> {
            LockSupport.unpark(t1);
            log.debug("2");
        }, "t2");

        t2.start();
    }

    public static void main(String[] args) {
        method1();
    }
}
