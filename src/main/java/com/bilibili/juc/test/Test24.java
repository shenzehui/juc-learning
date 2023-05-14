package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * Created by szh on 2023-05-14
 *
 * @author szh
 */

@Slf4j(topic = "c.Test24")
public class Test24 {

//    static ReentrantLock lock = new ReentrantLock();
//
//    public static void main(String[] args) throws InterruptedException {
//
//        // 创建一个新的条件变量(休息室)
//        Condition condition1 = lock.newCondition();
//
//        Condition condition2 = lock.newCondition();
//
//        lock.lock();
//
//        // 进入休息室等待(await 之前必须获取锁)
//        condition1.await(); // await 执行后，会释放锁
//
//        // 唤醒 await 的线程
//        condition1.signal();
//        condition1.signalAll();
//
//    }
    /**
     * 使用 ReentrantLock 条件变量
     */

    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    static ReentrantLock ROOM = new ReentrantLock();

    /**
     * 等待烟的休息室
     */
    static Condition waitCigaretteSet = ROOM.newCondition();

    /**
     * 等待外卖的休息室
     */
    static Condition waitTakeoutSet = ROOM.newCondition();

    public static void main(String[] args) {

        new Thread(() -> {
            ROOM.lock();
            try {
                log.debug("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        waitCigaretteSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("可以开始干活了");
            } finally {
                ROOM.unlock();
            }

        }, "小南").start();

        new Thread(() -> {
            ROOM.lock();
            try {
                log.debug("外卖送到没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        waitTakeoutSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("可以开始干活了");
            } finally {
                ROOM.unlock();
            }
        }, "小女").start();

        sleep(1);
        new Thread(() -> {
            ROOM.lock();
            try {
                hasTakeout = true;
                waitTakeoutSet.signal();
            } finally {
                ROOM.unlock();
            }
        }, "送外卖的").start();

        sleep(1);
        new Thread(() -> {
            ROOM.lock();
            try {
                hasCigarette = true;
                waitCigaretteSet.signal();
            } finally {
                ROOM.unlock();
            }
        }, "送烟的").start();

    }
}
