package com.bilibili.juc.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * cyclicbarrier 问题
 *
 * @author szh
 */
@Slf4j(topic = "c.TestCyclicBarrier")
public class TestCyclicBarrier {

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);
        // 支持可重用
        CyclicBarrier barrier = new CyclicBarrier(2, () -> {
            log.debug("task1,task2 finish...");
        });
        for (int i = 0; i < 3; i++) {
            service.submit(() -> {
                log.debug("task1 begin...");
                sleep(1);
                try {
                    barrier.await(); // 2-1 =1 若不为 0 则会阻塞在此
                    log.debug("task1 end...");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
            service.submit(() -> {
                log.debug("task2 begin...");
                sleep(1);
                try {
                    barrier.await(); // 1-1 = 0 task1、task2 都可以继续向下运行！
                    log.debug("task2 end...");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        service.shutdown();

    }

    private static void test1() {
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 3; i++) {
            CountDownLatch latch = new CountDownLatch(2);
            service.submit(() -> {
                log.debug("task1 start...");
                sleep(1);
                latch.countDown();
            });
            service.submit(() -> {
                log.debug("task2 start...");
                sleep(2);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("task1 task2 finish...");
        }
        service.shutdown();
    }
}
