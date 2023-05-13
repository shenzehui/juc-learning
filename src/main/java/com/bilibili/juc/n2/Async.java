package com.bilibili.juc.n2;

import com.bilibili.juc.n2.util.FileReader;
import lombok.extern.slf4j.Slf4j;


/**
 * @author szh
 */
@Slf4j(topic = "c.Async")
public class Async {

    public static void main(String[] args) {
        new Thread(() -> FileReader.read(Constants.MP4_FULL_PATH)).start();
        log.debug("do other things ...");
    }

}
