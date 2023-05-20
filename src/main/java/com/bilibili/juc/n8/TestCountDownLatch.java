package com.bilibili.juc.n8;

import com.bilibili.juc.n2.util.Sleeper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * CountDownLatch 等待多线程准备完毕
 *
 * @author szh
 */
@Slf4j(topic = "c.TestCountDownLatch")
public class TestCountDownLatch {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        String[] all = new String[10];
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            int key = i;
            service.submit(() -> {
                for (int j = 0; j <= 100; j++) {
                    try {
                        Thread.sleep(r.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    all[key] = j + "%";
                    // \r 是后面的打印覆盖前面的打印
                    System.out.print("\r" + Arrays.toString(all));
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("\n游戏开始");
        service.shutdown();
    }

    /**
     * 使用线程池
     */
    private static void test2() {
        CountDownLatch latch = new CountDownLatch(3);
        ExecutorService service = Executors.newFixedThreadPool(4);
        // 执行4个任务
        service.submit(() -> {
            log.debug("begin...");
            Sleeper.sleep(1);
            latch.countDown();
            log.debug("end...{}", latch.getCount());
        });
        service.submit(() -> {
            log.debug("begin...");
            Sleeper.sleep(2);
            latch.countDown();
            log.debug("end...,{}", latch.getCount());
        });
        service.submit(() -> {
            log.debug("begin...");
            Sleeper.sleep(1.5);
            latch.countDown();
            log.debug("end...,{}", latch.getCount());
        });

        service.submit(() -> {
            try {
                log.debug("waiting...");
                latch.await();
                log.debug("wait end...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * CountDawnLatch 基本用法
     *
     * @throws InterruptedException
     */
    private static void test() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        new Thread(() -> {
            log.debug("begin...");
            Sleeper.sleep(1);
            latch.countDown();
            log.debug("end...");
        }).start();

        new Thread(() -> {
            log.debug("begin...");
            Sleeper.sleep(2);
            latch.countDown();
            log.debug("end...");
        }).start();

        new Thread(() -> {
            log.debug("begin...");
            Sleeper.sleep(1.5);
            latch.countDown();
            log.debug("end...");
        }).start();

        log.debug("waiting...");
        // 等待计数变成 0
        latch.await();
        log.debug("wait end...");
    }

    /**
     * CountDownLatch 等待多个远程调用结束
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void test3() throws InterruptedException, ExecutionException {
        RestTemplate restTemplate = new RestTemplate();
        log.debug("begin");
        ExecutorService service = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(4);
        Future<Map<String, Object>> f1 = service.submit(() -> {
            Map<String, Object> response = restTemplate.getForObject("http://localhost:8080/order/{1}", Map.class, 1);
            return response;
        });
        Future<Map<String, Object>> f2 = service.submit(() -> {
            Map<String, Object> response1 = restTemplate.getForObject("http://localhost:8080/product/{1}", Map.class, 1);
            return response1;
        });
        Future<Map<String, Object>> f3 = service.submit(() -> {
            Map<String, Object> response2 = restTemplate.getForObject("http://localhost:8080/product/{1}", Map.class, 2);
            return response2;
        });
        Future<Map<String, Object>> f4 = service.submit(() -> {
            Map<String, Object> response3 = restTemplate.getForObject("http://localhost:8080/logistics/{1}", Map.class, 1);
            return response3;
        });

        // future 线程执行后的结果
        System.out.println(f1.get());
        System.out.println(f2.get());
        System.out.println(f3.get());
        System.out.println(f4.get());
        log.debug("执行完毕");
        service.shutdown();
    }
}
