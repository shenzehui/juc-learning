package com.bilibili.juc.n4;

/**
 * 单例模式：double checked locking 问题
 * synchronized 可以解决有序性、可见性和原子性问题，但前提是变量需要完全被 synchronized 包裹，若不是，则会发生指令重排序问题，也就是有序性无法解决
 * volatile     可以解决有序性和可见性问题，但无法解决原子性问题
 * volatile读写屏障： 读取操作：在此之前会加一个读屏障：防止读屏障之后的代码跳到前面去
 * 写操作：在此之后加一个写屏障：防止写屏障之前的代码跳到后面去
 * Created by szh on 2023-05-16
 *
 * @author szh
 */

public class Singleton {

    public Singleton() {
    }

    /**
     * 使用读写屏障解决指令重排序问题
     */
    private static volatile Singleton singleton = null;

    public static Singleton getSingleton() {
        // 实例没有创建，才会进入内部的 synchronized 代码块
        if (singleton == null) {
            synchronized (Singleton.class) {
                // 也许有其他线程已经创建实例，所以再判断一次
                if (singleton == null) {
                    // 在此之后加一个写屏障：防止写屏障之前的代码跳到后面去，t1 线程会将这行代码执行完毕后，再去执行 t2 线程，解决了有序性问题
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
