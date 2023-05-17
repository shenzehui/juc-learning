package com.bilibili.juc.test;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 扣款问题
 *
 * @author szh
 */
public class TestAccount {
    public static void main(String[] args) {
        Account account = new AccountCas(10000);
        Account.demo(account);

        Account account1 = new AccountUnsafe(10000);
        Account.demo(account1);
    }
}

class AccountCas implements Account {

    /**
     * 原子整数
     */
    private AtomicInteger balance;

    public AccountCas(int balance) {
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

    /**
     * 使用 cas 解决因共享变量造成原子性的线程安全问题
     *
     * @param amount
     */
    @Override
    public void withdraw(Integer amount) {
//        while (true) {
//            // 获取余额最新值
//            int prev = balance.get();
//            // 修改后的余额
//            int next = prev - amount;
//            // 真正修改：比较并设置
//            boolean b = balance.compareAndSet(prev, next);
//            if (b) {
//                break;
//            }
//        }

        // 与上面的结果一致
        balance.getAndAdd(-1 * amount);
    }
}

class AccountUnsafe implements Account {

    private Integer balance;

    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        synchronized (this) {
            return balance;
        }
    }

    @Override
    public void withdraw(Integer amount) {
        // 加 synchronized 解决并发问题
        synchronized (this) {
            this.balance -= amount;
        }
    }
}

interface Account {
    /**
     * 获取余额
     *
     * @return
     */
    Integer getBalance();

    /**
     * 取款
     *
     * @param amount
     */
    void withdraw(Integer amount);

    /**
     * 方法内会启动 1000 个线程，每个线程做 -10 元 的操作
     * 如果初始余额为 10000 那么正确的结果应当是 0
     */
    static void demo(Account account) {
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(10);
            }));
        }
        long start = System.nanoTime();
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.getBalance() + " cost: " + (end - start) / 1000_000 + " ms");
    }
}
