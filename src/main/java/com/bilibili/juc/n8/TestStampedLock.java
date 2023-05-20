package com.bilibili.juc.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.StampedLock;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * StampedLock 演示
 * 支持乐观读  不支持条件变量 不支持可重入
 *
 * @author szh
 */
@Slf4j(topic = "c.TestStampedLock")
public class TestStampedLock {
    public static void main(String[] args) {
        DataContainerStamped dataContainer = new DataContainerStamped(1);

        // 读读并发
//        new Thread(() -> {
//            dataContainer.read(1);
//        }, "t1").start();
//        sleep(0.5);
//        new Thread(() -> {
//            dataContainer.read(0);
//        }, "t2").start();

        // 读写并发
        new Thread(() -> {
            dataContainer.read(1);
        }, "t1").start();
        sleep(0.5);
        new Thread(() -> {
            dataContainer.write(1000);
        }, "t2").start();


    }
}

@Slf4j(topic = "c.DataContainerStamped")
class DataContainerStamped {
    private int data;
    private final StampedLock lock = new StampedLock();

    public DataContainerStamped(int data) {
        this.data = data;
    }

    public int read(int readTime) {
        // 先获取戳，做乐观读
        long stamp = lock.tryOptimisticRead();
        log.debug("optimistic read locking...{}", stamp);
        sleep(readTime);
        // 校验戳
        if (lock.validate(stamp)) {
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        }
        // 戳失效，乐观读锁升级为读锁（这里读写并发后会执行）
        // 锁升级 - 读锁
        log.debug("updating to read lock... {}", stamp);
        try {
            stamp = lock.readLock();
            log.debug("read lock {}", stamp);
            sleep(readTime);
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        } finally {
            log.debug("read unlock {}", stamp);
            lock.unlockRead(stamp);
        }
    }

    public void write(int newData) {
        // 加写锁
        long stamp = lock.writeLock();
        log.debug("write lock {}", stamp);
        try {
            sleep(2);
            this.data = newData;
        } finally {
            log.debug("write unlock {}", stamp);
            // 释放写锁
            lock.unlockWrite(stamp);
        }
    }
}
