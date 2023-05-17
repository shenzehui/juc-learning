package com.bilibili.juc.test;

import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Unsafe cas相关方法
 *
 * @author szh
 */
public class TestUnsafe {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        // 通过反射获取 Unsafe 类
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        // 允许访问私有成员变量
        theUnsafe.setAccessible(true);
        // 因为 Unsafe 是静态变量，所以从属于类，而不是对象，所以传递一个 null 值
        Unsafe unsafe = ((Unsafe) theUnsafe.get(null));
        System.out.println(unsafe);

        // 1. 获取到域的偏移地址
        long idOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
        long nameOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));

        // 2. 执行 cas 操作 参数：对象 域的偏移量 旧址 新值
        Teacher t = new Teacher();
        unsafe.compareAndSwapInt(t, idOffset, 0, 1);
        unsafe.compareAndSwapObject(t, nameOffset, null, "张三");

        // 3. 验证
        System.out.println(t);
    }

}

@Data
class Teacher {
    volatile int id;
    volatile String name;
}


