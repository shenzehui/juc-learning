package com.bilibili.juc.test;

import com.bilibili.juc.n2.Constants;
import com.bilibili.juc.n2.util.FileReader;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by szh on 2023-05-12
 *
 * @author szh
 */

@Slf4j(topic = "c.Test4")
public class Test4 {
    public static void main(String[] args) {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("running....");
//                FileReader.read(Constants.MP4_FULL_PATH);
            }
        };
        // run() 方法当作普通方法的方式调用  start() 方法来启动线程，真正实现了多线程运行。
        t1.start();
        log.debug("do other things...");
    }

}
