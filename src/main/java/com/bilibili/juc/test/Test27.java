package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

/**
 * 交替输出
 * Created by szh on 2023-05-15
 *
 * @author szh
 */

@Slf4j(topic = "c.Test27")
public class Test27 {
    public static void main(String[] args) {
        WaitNotify wn = new WaitNotify(1, 5);
        new Thread(() -> {
            wn.print("a", 1, 2);
        }).start();
        new Thread(() -> {
            wn.print("b", 2, 3);
        }).start();
        new Thread(() -> {
            wn.print("c", 3, 1);
        }).start();
    }

}

/**
 * 输出内容    等待标记    下一个标记
 * a          1          2
 * b          2          3
 * c          3          1
 */

class WaitNotify {
    /**
     * 等待标记
     */
    private int flag;

    /**
     * 循环次数
     */
    private int loopNumber;

    public WaitNotify(int flag, int loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }

    /**
     * 打印    a            1             2
     *
     * @param str
     * @param waitFlag
     * @param nextFlag
     */
    public void print(String str, int waitFlag, int nextFlag) {
        for (int i = 0; i < loopNumber; i++) {
            synchronized (this) {
                while (flag != waitFlag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(str);
                flag = nextFlag;
                this.notifyAll();
            }
        }
    }
}
