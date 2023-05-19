package com.bilibili.juc.n8;

import com.bilibili.juc.n2.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * aqs 自定义锁(不可重入)
 * Created by szh on 2023-05-19
 *
 * @author szh
 */

@Slf4j(topic = "c.TestAqs")
public class TestAqs {

    public static void main(String[] args) {
        MyLock lock = new MyLock();

        new Thread(() -> {
            lock.lock();
            try {
                log.debug("locking....");
                Sleeper.sleep(1);
            } finally {
                log.debug("unlocking...");
                lock.unlock();
            }
        }, "t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                log.debug("locking....");
            } finally {
                log.debug("unlocking...");
                lock.unlock();
            }
        }, "t2").start();

    }
}

/**
 * 自定义锁，不可重入锁
 */
class MyLock implements Lock {

    /**
     * 独占锁
     * 同步器类
     */
    class MySync extends AbstractQueuedSynchronizer {

        /**
         * 尝试加锁
         *
         * @param arg
         * @return
         */
        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0, 1)) {
                // 加上了锁，并设置 owner 为当前线程，这里跟监视器很像
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 尝试释放锁
         *
         * @param arg
         * @return
         */
        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            // volatile 确保前面的非 volatile 参数都会写到主存
            setState(0);
            return true;
        }

        /**
         * 是否持有独占锁
         *
         * @return
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition() {
            return new ConditionObject();
        }
    }

    private MySync sync = new MySync();

    /**
     * 加锁（不成功，会进入等待队列等待）
     */
    @Override
    public void lock() {
        sync.acquire(1);
    }

    /**
     * 加锁，可打断
     *
     * @throws InterruptedException
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    /**
     * 尝试加锁
     *
     * @return
     */
    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    /**
     * 尝试加锁，有超时时间
     *
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(time));
    }

    /**
     * 解锁
     */
    @Override
    public void unlock() {
        sync.release(1);
    }

    /**
     * 创建条件变量
     *
     * @return
     */
    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
