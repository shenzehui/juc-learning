package com.bilibili.juc.test;

import com.bilibili.juc.n2.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * 生产者消费者设计模式
 * Created by szh on 2023-05-14
 *
 * @author szh
 */

@Slf4j(topic = "c.Test21")
public class Test21 {
    public static void main(String[] args) throws InterruptedException {
        MessageQueue queue = new MessageQueue(2);

        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(() -> {
                queue.put(new Message(id, "值" + id));
            }, "生产者" + i).start();
        }

        Thread consumer = new Thread(() -> {
            while (true) {
                Sleeper.sleep(1);
                Message message = queue.take();
            }
        }, "消费者");
        consumer.start();
    }
}

/**
 * 消息队列类，java 线程之间通信
 */
@Slf4j(topic = "c.MessageQueue")
class MessageQueue {

    /**
     * 消息的队列集合
     */
    private LinkedList<Message> list = new LinkedList<>();

    /**
     * 消息容量
     */
    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 获取消息的方法
     */
    public Message take() {
        synchronized (list) {
            // 检查队列是否为空
            while (list.isEmpty()) {
                try {
                    log.debug("队列为空，消费者消息等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 从队列的头部获取消息并返回
            Message message = list.removeFirst();
            log.debug("已消费消息: {}", message);
            list.notifyAll();
            return message;
        }
    }

    /**
     * 存入消息
     */
    public void put(Message message) {
        synchronized (list) {
            // 检查队列是否已满
            while (list.size() == capacity) {
                try {
                    log.debug("队列已满，生产者消息等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            list.addLast(message);
            log.debug("已生产消息: {}", message);
            list.notifyAll();
        }
    }
}

final class Message {
    private int id;
    private Object value;

    public Message(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", value=" + value + '}';
    }
}
