package com.bilibili.juc.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * Executors.newCachedThreadPool() 底层实现：创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
 * SynchronousQueue 没有容量，没有线程来取是放不进去的
 * 适合场景：任务数比较密集，但每个任务执行时间较短的情况
 *
 * @author szh
 */
@Slf4j(topic = "c.TestSynchronousQueue")
public class TestSynchronousQueue {
    public static void main(String[] args) {
        SynchronousQueue<Integer> integers = new SynchronousQueue<>();
        new Thread(() -> {
            try {
                log.debug("putting {} ", 1);
                integers.put(1);
                log.debug("{} putted...", 1);

                log.debug("putting...{} ", 2);
                integers.put(2);
                log.debug("{} putted...", 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        sleep(1);

        new Thread(() -> {
            try {
                log.debug("taking {}", 1);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();

        sleep(1);

        new Thread(() -> {
            try {
                log.debug("taking {}", 2);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t3").start();
    }
}
