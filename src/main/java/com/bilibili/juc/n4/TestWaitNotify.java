package com.bilibili.juc.n4;

import lombok.extern.slf4j.Slf4j;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * wait notify
 *
 * @author szh
 */
@Slf4j(topic = "c.TestWaitNotify")
public class TestWaitNotify {
    final static Object obj = new Object();

    public static void main(String[] args) {

        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    // 注意 wait 会释放锁
                    obj.wait(1000); // 等待一秒钟， sleep(1000) 不会释放锁，但是 wait 会释放锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        }, "t1").start();

        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait(); // 让线程在 obj 上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        }, "t2").start();

        // 主线程两秒后执行
        sleep(0.5);
        log.debug("唤醒 obj 上其它线程");
        synchronized (obj) {
//            obj.notify(); // 唤醒 obj 上一个线程
            obj.notifyAll(); // 唤醒 obj 上所有等待线程
        }
    }
}
