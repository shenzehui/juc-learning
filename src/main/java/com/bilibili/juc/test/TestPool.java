package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义线程池
 * Created by szh on 2023-05-17
 *
 * @author szh
 */

@Slf4j(topic = "c.TestPool")
public class TestPool {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(1, 1000, TimeUnit.MICROSECONDS, 1, (queue, task) -> {
            // 1. 死等
//            queue.put(task);
            // 2. 待超时的等待
//            queue.offer(task,1500,TimeUnit.MILLISECONDS);
            // 3. 让调用者放弃任务的执行
//            log.debug("放弃{}", task);
            // 4. 让调用者抛出异常
//            throw new RuntimeException("任务执行失败" + task);
            // 5. 让调用者自己执行任务
            task.run();
        });
        for (int i = 0; i < 3; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}", j);
            });
        }
    }
}

@FunctionalInterface
interface RejectPolicy<T> {
    /**
     * 拒绝策略实现
     *
     * @param queue
     * @param task
     */
    void reject(BlockingQueue<T> queue, T task);
}

@Slf4j(topic = "c.ThreadPool")
class ThreadPool {

    /**
     * 任务队列
     */
    private BlockingQueue<Runnable> taskQueue;

    /**
     * 线程集合
     */
    private HashSet<Worker> workers = new HashSet();

    /**
     * 核心线程数
     */
    private int coreSize;

    /**
     * 获取任务的超时时间
     */
    private long timeout;

    /**
     * 时间单位
     */
    private TimeUnit timeUnit;


    private RejectPolicy<Runnable> rejectPolicy;

    /**
     * 执行任务
     */
    public void execute(Runnable task) {
        synchronized (workers) {
            // 当任务数没有超过 coreSize 时，直接交给 worker 对象执行
            // 如果任务数超过 coreSize 时，加入任务队列暂存
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.debug("新增 worker{}, {}", worker, task);
                workers.add(worker);
                worker.start();
            } else {
//                taskQueue.put(task);
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapacity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;
    }

    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 1. 当 task 不为空，执行任务
            // 2. 当 task 执行完毕，再接着从任务队列获取任务并执行
//            while (task != null || (task = taskQueue.task()) != null) {
            while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) {
                try {
                    log.debug("正在执行...{}", task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            synchronized (workers) {
                log.debug("worker 被移除{}", this);
                workers.remove(this);
            }
        }
    }
}

@Slf4j(topic = "c.BlockingQueue")
class BlockingQueue<T> {
    /**
     * 任务队列
     */
    private Deque<T> queue = new ArrayDeque<>();

    /**
     * 锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 生产者条件变量
     */
    private Condition fullWaitSet = lock.newCondition();

    /**
     * 消费者条件变量
     */
    private Condition emptyWaitSet = lock.newCondition();

    /**
     * 容量
     */
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 带超时的阻塞获取
     */
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            // 将 timeout 统一转化为纳秒
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {
                    if (nanos <= 0) {
                        // 已经超时，无需等待
                        return null;
                    }
                    // 返回的是剩余的时间
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞获取
     */
    public T task() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞添加
     */
    public void put(T element) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    log.debug("等待加入任务队列 {}...", element);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", element);
            queue.addLast(element);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时时间的阻塞添加
     *
     * @return
     */
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capacity) {
                try {
                    log.debug("等待加入任务队列 {}...", task);
                    if (nanos <= 0) {
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取大小
     */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            // 判断队列是否已满
            if (queue.size() == capacity) {
                rejectPolicy.reject(this, task);
            } else { // 有空闲
                log.debug("加入任务队列：{}", task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}
