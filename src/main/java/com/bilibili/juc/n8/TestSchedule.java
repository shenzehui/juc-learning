package com.bilibili.juc.n8;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 线程池应用 - 定时任务
 *
 * @author szh
 */
public class TestSchedule {

    /**
     * 如何让每周四 18:00:00 定时执行任务？
     */
    public static void main(String[] args) {


        // initialDelay 代表当前时间和周四的时间差
        LocalDateTime now = LocalDateTime.now(); // 当前时间
        System.out.println(now);

        // 获取本周周四时间
        LocalDateTime time = now.withHour(18).withMinute(0).withSecond(0).withNano(0).with(DayOfWeek.THURSDAY);

        // 如果当前时间大于本周周四，那必须得到下周周四
        if (now.compareTo(time) > 0) {
            // 加一周
            time.plusWeeks(1);
        }

        long initialDelay = Duration.between(now, time).toMillis();

        System.out.println(initialDelay);

        // period 一周的时间间隔
        int period = 1000 * 60 * 60 * 24 * 7;
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleAtFixedRate(() -> {
            System.out.println("running....");
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }
}
