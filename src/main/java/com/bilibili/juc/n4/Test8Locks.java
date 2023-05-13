package com.bilibili.juc.n4;

import lombok.extern.slf4j.Slf4j;

import static com.bilibili.juc.n2.util.Sleeper.sleep;

/**
 * 线程八锁
 *
 * @author szh
 */
@Slf4j(topic = "c.Test8Locks")
public class Test8Locks {
    public static void main(String[] args) {
        Number n1 = new Number();
        Number n2 = new Number();
        new Thread(() -> {
            log.debug("begin");
            n1.a();
        }).start();
        new Thread(() -> {
            log.debug("begin");
            n2.b();
        }).start();
//        new Thread(() -> {
//            log.debug("begin");
//            n1.c();
//        }).start();

    }
}

@Slf4j(topic = "c.Number")
class Number {

    // 作用同一个类
    public static synchronized void a() {
        sleep(1);
        log.debug("1");
    }

    // 作用于同一个实例对象
    public synchronized void b() {
        log.debug("2");
    }

//    public void c() {
//        log.debug("3");
//    }
}
