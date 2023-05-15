package com.bilibili.juc.test;

import com.bilibili.juc.n2.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * 内存可见性问题举例：一个线程对主存中的变量修改，这个变量对另一个线程不可见！
 * JIT编译器会将 run 的值缓存到 t1 线程的高速缓存中去，减少了对主存 run 的访问，提高效率
 * 线程 t1 是从自己内存的高速缓存中读取这个变量的值，结果永远是旧值。
 * <p>
 * volatile 是修饰成员变量和静态成员变量的，必须到主存中获取这个值
 * <p>
 * Created by szh on 2023-05-15
 *
 * @author szh
 */

@Slf4j(topic = "c.Test32")
public class Test32 {

    /**
     * 存在主内存中
     */
    static boolean run = true;

    /**
     * 锁对象
     */
    final static Object lock = new Object();

//    static boolean run = true;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (run) {
                //...

                // synchronized 也可以解决可见性
                synchronized (lock) {
                    if (!run) {
                        break;
                    }
                }
            }
        });
        t1.start();

        Sleeper.sleep(1);
        log.debug("停止 t");

        synchronized (lock) {
            run = false; // 线程 t 不会如预想的停下来
        }
    }
}
