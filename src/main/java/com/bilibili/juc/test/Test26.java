package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 可重入锁解决固定运行顺序问题
 * Created by szh on 2023-05-14
 *
 * @author szh
 */

@Slf4j(topic = "c.Test26")
public class Test26 {
    private static ReentrantLock lock = new ReentrantLock();

    private static Condition condition = lock.newCondition();


    /**
     * 表示 t2 是否运行过
     */
    static boolean t2runned = false;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                while (!t2runned) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("1");
            } finally {
                lock.unlock();
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            lock.lock();
            try {
                while (t2runned) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                condition.signalAll();
                t2runned = true;
                log.debug("2");
            } finally {
                lock.unlock();
            }
        }, "t2");

        t1.start();

        t2.start();

    }
}
