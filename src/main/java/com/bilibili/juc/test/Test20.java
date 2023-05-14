package com.bilibili.juc.test;

import com.bilibili.juc.n2.util.Sleeper;
import com.bilibili.juc.pattern.Downloader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * join 代码底层
 * 设计模式-保护性暂停-实现
 * GuardObject
 * Created by szh on 2023-05-14
 *
 * @author szh
 */


@Slf4j(topic = "c.Test20")
public class Test20 {
    /**
     * 线程一等待线程二的下载结果
     *
     * @param args
     */
//    public static void main(String[] args) {
//        GuardObject guardObject = new GuardObject();
//        new Thread(() -> {
//            log.debug("等待结果");
//            Object response = guardObject.getResponse(2000);
//            log.debug("结果是:{}", response);
//        }, "t1").start();
//
//        new Thread(() -> {
//            log.debug("执行下载");
//            try {
//                List<String> list = Downloader.download();
//                Sleeper.sleep(3);
//                // 传递结果
//                guardObject.complete(new Object());
////                log.debug("结果的大小:{}", list.size());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }, "t2").start();
//    }
    public static void main(String[] args) {
        // 三个居民等待收信
        for (int i = 0; i < 3; i++) {
            new People().start();
        }
        Sleeper.sleep(1);
        // 三个邮递员根据信件 id 发送信
        for (Integer id : Mailboxes.getIds()) {
            new Postman(id, "内容" + id).start();
        }
    }

}

@Slf4j(topic = "c.People")
class People extends Thread {
    @Override
    public void run() {
        // 收信
        GuardObject object = Mailboxes.createGuardObject();
        log.debug("开始收信 id:{}", object.getId());
        Object mail = object.getResponse(5000);
        log.debug("收到信 id:{},内容:{}", object.getId(), mail);
    }
}

@Slf4j(topic = "c.Postman")
class Postman extends Thread {

    private int id;

    private String mail;

    public Postman(int id, String mail) {
        this.id = id;
        this.mail = mail;
    }


    @Override
    public void run() {
        GuardObject guardedObject = Mailboxes.getGuardedObject(id);
        log.debug("送信 id :{}, 内容:{}", id, mail);
        guardedObject.complete(mail);
    }
}

class Mailboxes {
    // fixme 可以用其他安全的 Map 代替
    private static Map<Integer, GuardObject> boxes = new Hashtable<>();

    private static int id = 1;

    public static GuardObject getGuardedObject(int id) {
        return boxes.remove(id);
    }

    /**
     * 产生唯一 id
     *
     * @return
     */
    public static synchronized int generateId() {
        return id++;
    }

    public static GuardObject createGuardObject() {
        GuardObject go = new GuardObject(generateId());
        boxes.put(go.getId(), go);
        return go;
    }

    public static Set<Integer> getIds() {
        return boxes.keySet();
    }

}


class GuardObject {

    /**
     * 唯一标识
     */
    private int id;

    public GuardObject(int id) {
        this.id = id;
    }

    public GuardObject() {
    }

    public int getId() {
        return id;
    }

    /**
     * 结果
     */
    private Object response;

    /**
     * 获取结果
     *
     * @param timeout 最大等待时间
     * @return
     */
    public Object getResponse(long timeout) {
        synchronized (this) {
            // 开始时间
            long begin = System.currentTimeMillis();
            // 记录经历的时间
            long passTime = 0;
            // 没有结果
            while (response == null) {
                // 这一轮应该等待的时间
                long waitTime = timeout - passTime;
                // 经历的时间超出了最大等待时间，退出循环
                if (waitTime <= 0) {
                    break;
                }
                try {
                    this.wait(waitTime); // 避免虚假唤醒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 求得经历的时间
                passTime = System.currentTimeMillis() - begin;

            }
            return response;
        }
    }

    /**
     * 产生结果
     */
    public void complete(Object response) {
        synchronized (this) {
            // 给结果成员变量赋值
            this.response = response;
            // 唤醒等待线程
            this.notifyAll();
        }

    }
}
