package com.bilibili.juc.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Created by szh on 2023-05-17
 *
 * 原子更新器：修改一个 volatile 修饰的变量
 * @author szh
 */

@Slf4j(topic = "c.Test40")
public class Test40 {
    public static void main(String[] args) {
        Student stu = new Student();

        // integer 类型
//        AtomicIntegerFieldUpdater

        AtomicReferenceFieldUpdater updater = AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");

        System.out.println(updater.compareAndSet(stu, null, "张三"));

        System.out.println(stu);
    }
}

class Student {
    volatile String name;

    @Override
    public String toString() {
        return "Student{" + "name='" + name + '\'' + '}';
    }
}
