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
        TowPhaseTermination tpt = new TowPhaseTermination();
        tpt.start();

        TimeUnit.SECONDS.sleep(4);
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
