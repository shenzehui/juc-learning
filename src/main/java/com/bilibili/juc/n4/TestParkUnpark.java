package com.bilibili.juc.n4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

import static com.bilibili.juc.n2.util.Sleeper.sleep;


/**
 * park unpark
 * 与 wait 和 notify 相比
 * wait,notify,notifyAll 必须配置 Monitor 使用，而 park，unpark 不必
 * park，unpark 是以线程为单位的阻塞和唤醒线程，而 notify 只能随机唤醒一个等待线程，notifyAll 是唤醒所有等待线程，不那么精确
 *
 * @author szh
 */
@Slf4j(topic = "c.TestParkUnpark")
public class TestParkUnpark {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            log.debug("start...");
            sleep(2);
            log.debug("park...");
            LockSupport.park();
            log.debug("resume...");
        }, "t1");
        t1.start();

        sleep(1);
        log.debug("unpark...");
        LockSupport.unpark(t1);
    }
}
