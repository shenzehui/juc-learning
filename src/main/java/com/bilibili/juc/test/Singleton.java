package com.bilibili.juc.test;

/**
 * Created by szh on 2023-05-16
 *
 * @author szh
 */

// 问题1：枚举单例是如何限制实例个数的     枚举类中的静态成员变量，是单实例的
// 问题2：枚举单例在创建时是否有并发问题   没有，是 static，是由类加载时创建的
// 问题3：枚举单例能否被反射破坏单例       不能
// 问题4：枚举单例能否被反序列化破坏单例   不能
// 问题5：枚举单例属于懒汉式还是饿汉式     饿汉式
// 问题6：枚举单例如果希望加入一些单例创建时的初始化逻辑该如何做   写个构造方法
public enum Singleton {
    INSTANCE;
}