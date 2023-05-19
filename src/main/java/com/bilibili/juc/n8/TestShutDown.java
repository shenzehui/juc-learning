package com.bilibili.juc.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

/**
 * shutdown 方法
 *
 * @author szh
 */
@Slf4j(topic = "c.TestShutDown")
public class TestShutDown {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        Future<Integer> result1 = pool.submit(() -> {
            log.debug("task 1 running...");
            Thread.sleep(1000);
            log.debug("task 1 finish...");
            return 1;
        });

        Future<Integer> result2 = pool.submit(() -> {
            log.debug("task 2 running...");
            Thread.sleep(1000);
            log.debug("task 2 finish...");
            return 2;
        });

        Future<Integer> result3 = pool.submit(() -> {
            log.debug("task 3 running...");
            Thread.sleep(1000);
            log.debug("task 3 finish...");
            return 3;
        });

//        log.debug("shutdown");
        // shutdown 不会影响线程池中的正在运行或者不在运行的线程任务，也不会阻塞主线程，但是如果在 shutdown 之后还想通过线程池运行任务，这是不允许的
//        pool.shutdown();

        // 这个方法区别于 shutdown 的是，他会阻塞住主线程，等到过了超时时间或者线程都运行完毕后，再执行主线程后面的代码
//        pool.awaitTermination(3, TimeUnit.SECONDS);
//
//        log.debug("other....");

        // 正在运行的任务都会被打断，返回的是没有运行的任务，你可以获取后自己做处理~
        List<Runnable> runnables = pool.shutdownNow();
        log.debug("other.... {}", runnables);

    }
}
