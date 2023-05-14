package com.bilibili.juc.n4;

import lombok.extern.slf4j.Slf4j;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * 使用 sleep
 * @author szh
 */
@Slf4j(topic = "c.TestCorrectPosture")
public class TestCorrectPostureStep1 {

    static final Object room = new Object();

    static boolean hasCigarette = false; // 有没有烟

    static boolean hasTakeout = false;

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    sleep(2);
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        // 等待小南释放锁再执行
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        sleep(1);
        new Thread(() -> {
            // 这里能不能加 synchronized (room)？ 不能，sleep 不会释放锁，无法获取 room 这把锁
//            synchronized (room) {
            hasCigarette = true;
            log.debug("烟到了噢！");
//            }
        }, "送烟的").start();
    }

}
