package com.bilibili.juc.test;

import com.bilibili.juc.n2.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock
 * Created by szh on 2023-05-14
 *
 * @author szh
 */

@Slf4j(topic = "c.Test22")
public class Test22 {

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        // tryLock() 方式获取锁
        Thread t1 = new Thread(() -> {
            log.debug("尝试获取锁");
            try {
                if (!lock.tryLock(2, TimeUnit.SECONDS)) {
                    log.debug("获取不到锁");
                    return;
                }
            } catch (Exception e) {
                log.debug("获取不到锁");
                e.printStackTrace();
                return;
            }
            try {
                log.debug("获得到锁");
            } finally {
                lock.unlock();
            }

        }, "t1");
        // 因为这里是不同线程，所以获取到锁，如果是同一线程，就可以重入，可获得到锁
        lock.lock();

        log.debug("获得到锁");
        t1.start();

        Sleeper.sleep(1);

        lock.unlock();
        log.debug("释放了锁");

    }

    /**
     * lock.lockInterruptibly() 可打断：防止死锁
     * lock.lock() 不可打断
     */
    public static void test2() {
        Thread t1 = new Thread(() -> {
            // 如果没有竞争，那么此方法就会获取 lock 对象锁
            // 如果有竞争就会进入阻塞队列，可以被其他线程用 interrupt 方法打断
            try {
                log.debug("尝试获取到锁");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获取到锁，返回");
                return;
            }

            try {
                log.debug("获取到锁");
            } finally {
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        // 获取不到锁
        t1.start();

        Sleeper.sleep(1);

        log.debug("打断 t1");

        t1.interrupt();
    }


    /**
     * ReentrantLock 可重入演示
     * 可重入：同一个线程首次获得了这把锁，那么有权利再次获取这把锁
     */
    public static void test1() {

        lock.lock();
        try {
            log.debug("enter main");
            m1();
        } finally {
            lock.unlock();
        }
    }

    public static void m1() {
        lock.lock();
        try {
            log.debug("enter m1");
            m2();
        } finally {
            lock.unlock();
        }
    }

    public static void m2() {
        lock.lock();
        try {
            log.debug("enter m2");
        } finally {
            lock.unlock();
        }
    }
}
