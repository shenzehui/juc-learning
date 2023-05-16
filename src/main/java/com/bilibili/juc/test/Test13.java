package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 两阶段终止模式：优雅实现将一个线程的打断
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test13")
public class Test13 {
    public static void main(String[] args) throws InterruptedException {
        TowPhaseTermination2 tpt = new TowPhaseTermination2();
        tpt.start();

        TimeUnit.SECONDS.sleep(4);
        log.debug("停止监控");
        tpt.stop();
    }
}

@Slf4j(topic = "c.TowPhaseTermination")
class TowPhaseTermination {
    /**
     * 监控线程
     */
    private Thread monitor;


    /**
     * 启动监控线程
     */
    public void start() {
        monitor = new Thread(() -> {
            while (true) {
                Thread thread = Thread.currentThread();
                // 注意：thread.interrupt(); 会清除打断标记，所以不会产生 true 的情况
                if (thread.isInterrupted()) {
                    log.debug("料理后事");
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(1); // 情况1
                    log.debug("执行监控记录");          // 情况2
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 重新设置打断标记，避免因 sleep 中的线程被打断，将 thread.isInterrupted() 设置为 false
                    thread.interrupt();
                }
            }
        });
        monitor.start();
    }

    /**
     * 停止监控线程
     */
    public void stop() {
        monitor.interrupt();
    }
}

/**
 * 用 volatile 实现两阶段终止模式
 */
@Slf4j(topic = "c.TowPhaseTermination2")
class TowPhaseTermination2 {
    /**
     * 监控线程
     */
    private Thread monitor;

    /**
     * 终止标志
     */
    private volatile boolean stop = false;

    // 同步模式 balking 模式，保证只能启动一次
    /**
     * 判断是否执行过 start 方法
     */
    private boolean starting = false;

    /**
     * 启动监控线程
     */
    public void start() {
        synchronized (this) {
            if (starting) { // false，false
                return;
            }
            starting = true;
        }
        monitor = new Thread(() -> {
            while (true) {
                // 注意：thread.interrupt(); 会清除打断标记，所以不会产生 true 的情况
                if (stop) {
                    log.debug("料理后事");
                    break;
                }
                try {
                    Thread.sleep(1000);// 情况1
                    log.debug("执行监控记录");          // 情况2
                } catch (InterruptedException e) {
                    log.debug("在睡眠中被打断了");
                    e.printStackTrace();
                }
            }
        }, "monitor");
        monitor.start();
    }

    /**
     * 停止监控线程
     */
    public void stop() {
        stop = true;
        // 快速打断 sleep 中的线程
        monitor.interrupt();
    }
}
