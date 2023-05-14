package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 可重入演示
 * 可重入：同一个线程首次获得了这把锁，那么有权利再次获取这把锁
 * Created by szh on 2023-05-14
 *
 * @author szh
 */

@Slf4j(topic = "c.Test22")
public class Test22 {

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {

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
