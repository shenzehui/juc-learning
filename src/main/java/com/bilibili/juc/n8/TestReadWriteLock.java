package com.bilibili.juc.n8;

import com.bilibili.juc.n2.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReentrantReadWriteLock 实现读写锁
 * 读读不互斥
 * 读写互斥
 * 写写互斥
 * Created by szh on 2023-05-19
 *
 * @author szh
 */

public class TestReadWriteLock {
    public static void main(String[] args) throws InterruptedException {
        DataContainer dataContainer = new DataContainer();
        new Thread(() -> {
            dataContainer.write();
        }, "t1").start();

        Thread.sleep(100);
        new Thread(() -> {
            dataContainer.write();
        }, "t2").start();
    }
}

@Slf4j(topic = "c.DataContainer")
class DataContainer {

    private Object data;

    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    /**
     * 读锁
     */
    private ReentrantReadWriteLock.ReadLock r = rw.readLock();

    /**
     * 写锁
     */
    private ReentrantReadWriteLock.WriteLock w = rw.writeLock();

    public Object read() {
        log.debug("获取读锁...");
        r.lock();
        try {
            log.debug("读取");
            Sleeper.sleep(1);
            return data;
        } finally {
            log.debug("释放读锁...");
            r.unlock();
        }
    }

    public void write() {
        log.debug("获取写锁...");
        w.lock();
        try {
            log.debug("写入");
            Sleeper.sleep(1);
        } finally {
            log.debug("释放写锁...");
            w.unlock();
        }
    }
}


