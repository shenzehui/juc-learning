package com.bilibili.juc.test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.*;

/**
 * 使用 ReentrantLock 交替输出
 * Created by szh on 2023-05-15
 *
 * @author szh
 */

public class Test30 {
    public static void main(String[] args) throws InterruptedException {
        AwaitSignal awaitSignal = new AwaitSignal(5);
        Condition a = awaitSignal.newCondition();
        Condition b = awaitSignal.newCondition();
        Condition c = awaitSignal.newCondition();

        new Thread(() -> {
            awaitSignal.print("a", a, b);
        }).start();

        new Thread(() -> {
            awaitSignal.print("b", b, c);
        }).start();

        new Thread(() -> {
            awaitSignal.print("c", c, a);
        }).start();

        // 一秒钟之后
        sleep(1000);

        awaitSignal.lock();
        try {
            a.signal();
            System.out.println("开始...");
        } finally {
            awaitSignal.unlock();
        }

    }
}

class AwaitSignal extends ReentrantLock {
    private int loopNumber;

    public AwaitSignal(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    /**
     * 参数一：打印内容
     * 参数二：进入哪一间休息室
     * 参数三：下一间休息室
     */
    public void print(String str, Condition current, Condition next) {
        for (int i = 0; i < loopNumber; i++) {
            lock();
            try {
                current.await();
                System.out.print(str);
                next.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                unlock();
            }
        }
    }
}
