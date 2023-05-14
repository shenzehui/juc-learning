package com.bilibili.juc.test;

import com.bilibili.juc.n2.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * sleep 和 wait
 * 区别：
 * sleep 是 Thread 的方法，而 wait 是 Object 的方法
 * sleep 不需要控制和 synchronized 配合使用，但 wait 需要和 synchronized 一起使用
 * sleep 在睡眠的同时，不会释放对象锁，wait 在等待的时候会释放对象锁
 * 共同点：
 * 他们的状态都是 TIME_WAITING
 * Created by szh on 2023-05-14
 *
 * @author szh
 */

@Slf4j(topic = "c.Test19")
public class Test19 {
    static final Object lock = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (lock) {
                log.debug("获得锁");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Sleeper.sleep(2);
        synchronized (lock) {
            log.debug("获得锁");
        }
    }
}
