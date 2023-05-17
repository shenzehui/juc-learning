package com.bilibili.juc.test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 * AtomicInteger 的使用
 * Created by szh on 2023-05-16
 *
 * @author szh
 */

public class Test34 {
    public static void main(String[] args) {
        AtomicInteger integer = new AtomicInteger(5);

        // 这些方法本来就是原子性操作
//        System.out.println(integer.incrementAndGet()); // ++i
//        System.out.println(integer.getAndIncrement()); // i++

//        System.out.println(integer.getAndAdd(5));  // 2,7
//        System.out.println(integer.addAndGet(5));  // 12,12

        // value 读取到的值  箭头后就是设置的值
        integer.updateAndGet(value -> value * 10);

//        integer.getAndUpdate()

        updateAndGet(integer, p -> p / 2);


        System.out.println(integer.get());

    }

    /**
     * 优雅地手写一个 updateAndGet
     *
     * @param integer
     * @param operator
     */
    public static void updateAndGet(AtomicInteger integer, IntUnaryOperator operator) {
        while (true) {
            int prev = integer.get();
            int next = operator.applyAsInt(prev);
            if (integer.compareAndSet(prev, next)) {
                break;
            }
        }
    }
}
