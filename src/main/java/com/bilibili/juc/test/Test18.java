package com.bilibili.juc.test;

import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by szh on 2023-05-14
 *
 * @author szh
 */

@Slf4j(topic = "c.Test18")
public class Test18 {

    static final Object lock = new Object();

    public static void main(String[] args) {
        synchronized (lock) {
            try {
                // 该方法必须先获取该对象锁
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
