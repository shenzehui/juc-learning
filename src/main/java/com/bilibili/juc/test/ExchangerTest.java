package com.bilibili.juc.test;

import java.util.concurrent.*;

/**
 * Exchanger 进行线程数据交换
 * Created by szh on 2023-06-01
 *
 * @author szh
 */

public class ExchangerTest {
    private static final Exchanger<String> exgr = new Exchanger<>();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String A = "银行流水A"; // A 录入银行流水数据
                    String B = exgr.exchange(A);
                    System.out.println("A 交换后得到的数据:" + B);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String B = "银行流水B"; // B 录入银行流水数据
//                    exgr.exchange(B, 1000, TimeUnit.SECONDS);  // 避免一直等待阻塞
                    String A = exgr.exchange(B);
                    System.out.println("B 交换后得到的数据:" + A);
                    System.out.println("A和B数据是否一致：" + A.equals(B) + "，A 录入的是：" + A + "，B 录入的是:" + B);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadPool.shutdown();
    }
}
