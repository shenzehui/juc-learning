package com.bilibili.juc.n9;

import java.util.Date;

/**
 * Created by szh on 2023-06-12
 *
 * @author szh
 */

public class MyRunnable implements Runnable {

    private String command;

    public MyRunnable(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "State Time=" + new Date());
        processCommand();
        System.out.println(Thread.currentThread().getName() + "End Time" + new Date());
    }

    private void processCommand() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.command;
    }
}
