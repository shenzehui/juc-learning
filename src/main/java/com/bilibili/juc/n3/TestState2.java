package com.bilibili.juc.n3;

import com.bilibili.juc.n2.Constants;
import com.bilibili.juc.n2.util.FileReader;

/**
 * Java 中的阻塞也是运行状态
 * Created by szh on 2023-05-12
 *
 * @author szh
 */
public class TestState2 {
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            FileReader.read(Constants.MP4_FULL_PATH);
            FileReader.read(Constants.MP4_FULL_PATH);
            FileReader.read(Constants.MP4_FULL_PATH);
        }, "t1").start();

        Thread.sleep(1000);
        System.out.println("ok");
    }
}
