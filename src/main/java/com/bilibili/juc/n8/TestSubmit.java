package com.bilibili.juc.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * 提交任务的相关方法
 *
 * @author szh
 */
@Slf4j(topic = "c.TestSubmit")
public class TestSubmit {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        String result = pool.invokeAny(Arrays.asList(() -> {
            log.debug("begin 1");
            Thread.sleep(1000);
            log.debug("end 1");
            return "1";
        }, () -> {
            log.debug("begin 1");
            Thread.sleep(500);
            log.debug("end 1");
            return "2";
        }, () -> {
            log.debug("begin 2");
            Thread.sleep(2000);
            log.debug("end 3");
            return "3";
        }));

        log.debug("{}", result);
    }

    /**
     * 哪个任务先执行完毕，返回此任务的结果，其他任务取消
     *
     * @param pool
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void method3(ExecutorService pool) throws InterruptedException, ExecutionException {
        String result = pool.invokeAny(Arrays.asList(() -> {
            log.debug("begin 1");
            Thread.sleep(1000);
            log.debug("end 1");
            return "1";
        }, () -> {
            log.debug("begin 2");
            Thread.sleep(500);
            log.debug("end 2");
            return "2";
        }, () -> {
            log.debug("begin 3");
            Thread.sleep(2000);
            log.debug("end 3");
            return "3";
        }));
        log.debug("{}", result);
    }

    /**
     * invokeAll 提交 task 中的所有任务
     *
     * @param pool
     * @throws InterruptedException
     */
    private static void method2(ExecutorService pool) throws InterruptedException {
        List<Future<String>> futures = pool.invokeAll(Arrays.asList(() -> {
            log.debug("begin");
            Thread.sleep(1000);
            return "1";
        }, () -> {
            log.debug("begin");
            Thread.sleep(500);
            return "2";
        }, () -> {
            log.debug("begin");
            Thread.sleep(2000);
            return "3";
        }));

        futures.forEach(f -> {
            try {
                log.debug("{}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * submit 方法
     */
    private static void method1(ExecutorService pool) throws InterruptedException, ExecutionException {
        Future<String> future = pool.submit(() -> {
            log.debug("running");
            Thread.sleep(1000);
            return "ok";
        });

        log.debug("{}", future.get());
    }
}
