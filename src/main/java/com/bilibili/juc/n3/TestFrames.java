package com.bilibili.juc.n3;

/**
 * Java Virtual Machine Stacks （Java 虚拟机栈）
 * 先进后出
 * 线程的栈内存是相互独立的
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

public class TestFrames {
    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            method1(20);
        });
        t1.setName("t1");
        t1.start();
        method1(10);
    }

    private static void method1(int x) {
        int y = x + 1;
        Object m = method2();
        System.out.println(m);
    }

    private static Object method2() {
        return new Object();
    }
}
