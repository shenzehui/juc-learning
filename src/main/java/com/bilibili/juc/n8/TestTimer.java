package com.bilibili.juc.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

import static com.bilibili.juc.n2.util.Sleeper.sleep;


/**
 * Timer、ScheduledExecutorService 延时执行任务
 *
 * @author szh
 */
@Slf4j(topic = "c.TestTimer")
public class TestTimer {
    /**
     * ScheduledExecutorService-定时执行的两种方法
     *
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);

        log.debug("start...");
        pool.scheduleAtFixedRate(() -> {
            log.debug("running...");
            sleep(2);
            // 延时工作开始时长 和 时间间隔，这里是不管前一个任务的返回结果，1 秒后必循环执行
        }, 1, 1, TimeUnit.SECONDS);

        pool.scheduleWithFixedDelay(() -> {
            log.debug("running...");
            sleep(2);
            // 这里的 delay 是从前一个任务执行完毕后再执行等待的时间
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * ScheduledExecutorService-延时执行异常处理
     */
    private static void method3() {
        // 2 核心线程数
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);

        // 推荐使用这种方法，解决了 timer 的缺点
        ScheduledFuture<?> task1 = pool.schedule(() -> {
            try {
                log.debug("task1");
                int i = 1 / 0;           // 出现异常，也不会影响后面线程的执行
                // Thread.sleep(1000);   // 睡眠，也不会阻塞后面线程的执行
            } catch (Exception e) {
                e.printStackTrace();
                // 异常处理
                log.error("error", e);
            }

        }, 1, TimeUnit.SECONDS);

        pool.schedule(() -> {
            log.debug("task2");
        }, 1, TimeUnit.SECONDS);
    }

    /**
     * ScheduledExecutorService-延时执行
     *
     * @param pool
     */
    private static void method2(ScheduledExecutorService pool) {
        pool.schedule(() -> {
            log.debug("task1");
            int i = 1 / 0;
        }, 1, TimeUnit.SECONDS);

        pool.schedule(() -> {
            log.debug("task2");
        }, 1, TimeUnit.SECONDS);
    }

    private static void method1() {
        Timer timer = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 1");
//                int i = 1 / 0;  // 异常，后面的任务都无法执行
                sleep(2);       // sleep 睡眠也会影响后面任务的执行
            }
        };
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 2");
            }
        };

        log.debug("start...");
        timer.schedule(task1, 1000);
        timer.schedule(task2, 1000);
    }
}
