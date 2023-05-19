package com.bilibili.juc.n8;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Created by szh on 2023-05-19
 *
 * @author szh
 */

public class TestForkJoin {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool(4);
        System.out.println(pool.invoke(new MyTask(5)));

        // new MyTask(5)
    }
}

/**
 * RecursiveTask 递归任务
 * 1~n 之间整数的和
 */
@Slf4j(topic = "c.MyTask")
class MyTask extends RecursiveTask<Integer> {

    private int n;

    public MyTask(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "{" + n + "}";
    }

    @Override
    protected Integer compute() {
        if (n == 1) {
            log.debug("join() {}", n);
            return 1;
        }
        MyTask t1 = new MyTask(n - 1);
        // 这里执行 MyTask(4)，会再调用 compute 方法，递归，知道 n ==1 返回 1
        t1.fork(); // 让一个线程去执行此任务
        log.debug("fork() {} + {}", n, t1);

        Integer result = t1.join() + n;
        log.debug("join() {} + {} = {}", n, t1, result);
        return result;
    }
}
