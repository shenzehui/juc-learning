package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

/**
 * 偏向锁
 * Created by szh on 2023-05-13
 *
 * @author szh
 */
@Slf4j(topic = "c.TestBiased")
public class TestBiased {
    public static void main(String[] args) throws InterruptedException {

    }

    /**
     * 撤销偏向锁
     */
    private static void test2() {
        // 偏向锁默认是有延迟的
        Dog d = new Dog();
//        d.hashCode(); // 调用对象的 hashCode() 方法，会禁用这个对象的偏向锁

        new Thread(() -> {
            // 偏向锁、轻量级锁
            log.debug(ClassLayout.parseInstance(d).toPrintable());

            // 加锁打印对象头
            synchronized (d) {
                log.debug(ClassLayout.parseInstance(d).toPrintable());
            }

            // 解锁后打印对象头
            log.debug(ClassLayout.parseInstance(d).toPrintable()); // 还是偏向锁

            synchronized (TestBiased.class) {
                // 等 t1 线程执行完毕，t2 唤醒，解除等待
                TestBiased.class.notify();
            }
        }, "t1").start();


        new Thread(() -> {
            synchronized (TestBiased.class) {
                try {
                    // 这里先 t1 唤醒
                    TestBiased.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug(ClassLayout.parseInstance(d).toPrintable());

            synchronized (d) {
                // 之前偏向锁偏向于 t1 线程，现在 t2 线程要使用这个对象，偏向锁会失效，进入轻量级锁
                log.debug(ClassLayout.parseInstance(d).toPrintable());
            }

            log.debug(ClassLayout.parseInstance(d).toPrintable()); // 还是偏向锁
        }, "t2").start();
    }

    /**
     * 批量重偏向
     */
    private static void test3() {
        Vector<Dog> list = new Vector<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                Dog d = new Dog();
                list.add(d);
                // t1 线程第一次加的锁默认都是偏向锁
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            synchronized (list) {
                list.notify();
            }
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (list) {
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("===============> ");
            for (int i = 0; i < 30; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                // 前 20 个都是偏向于 t1 线程，后面都是偏向于 t2，不会撤销，不再偏向 t1 线程
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
        }, "t2");
        t2.start();
    }

    static Thread t1, t2, t3;

    // 批量
    private static void test4() throws InterruptedException {
        Vector<Dog> list = new Vector<>();

        int loopNumber = 39;
        // t1 线程占用 d 对象，默认加偏向锁
        t1 = new Thread(() -> {
            for (int i = 0; i < loopNumber; i++) {
                Dog d = new Dog();
                list.add(d);
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            LockSupport.unpark(t2);
        }, "t1");
        t1.start();

        // t2 线程因为线程改变，将偏向锁一个一个撤销成轻量级锁，从 20 开始，又开始批量重偏向，偏向于 t2
        t2 = new Thread(() -> {
            LockSupport.park();
            log.debug("===============> ");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
            LockSupport.unpark(t3);
        }, "t2");
        t2.start();

        // t3 线程 前 40 个都是批量撤销成轻量级锁，第40个变成不可偏向状态
        t3 = new Thread(() -> {
            LockSupport.park();
            log.debug("===============> ");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
        }, "t3");
        t3.start();

        t3.join();
        log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());
    }


}

class Dog {
}