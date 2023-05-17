package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * AtomicReference 无法感知其他线程对共享变量的修改 造成 ABA 问题
 * AtomicStampedReference 版本号解决 ABA 问题
 * @author szh
 */
@Slf4j(topic = "c.Test36")
public class Test36 {

    /**
     *  0 是初始版本号
     */
    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start...");
        // 获取值 A
        String prev = ref.getReference();
        // 获取版本号
        int stamp = ref.getStamp();
        log.debug("版本 {}", stamp);
        // 如果中间有其它线程干扰，发生了 ABA 现象
        other();
        sleep(1);
        // 尝试改为 C  参数说明：当前的版本号，修改后的版本号
        log.debug("change A->C {}", ref.compareAndSet(prev, "C", stamp, stamp + 1));  // false
    }

    private static void other() {
        new Thread(() -> {
            // 修改后版本号都需要加 1
            log.debug("change A->B {}", ref.compareAndSet(ref.getReference(), "B", ref.getStamp(), ref.getStamp() + 1));
            log.debug("更新版本为 {}", ref.getStamp());
        }, "t1").start();
        sleep(0.5);
        new Thread(() -> {
            // 修改后版本号都需要加 1
            log.debug("change B->A {}", ref.compareAndSet(ref.getReference(), "A", ref.getStamp(), ref.getStamp() + 1));
            log.debug("更新版本为 {}", ref.getStamp());
        }, "t2").start();
    }
}