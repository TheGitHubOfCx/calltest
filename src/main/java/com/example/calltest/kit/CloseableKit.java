package com.example.calltest.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseableKit {

    private static final Logger logger = LoggerFactory.getLogger(CloseableKit.class);

    private CloseableKit() {
    }

    public static void close(AutoCloseable s) {
        if(s != null) {
            try {
                s.close();
                s = null;
            } catch (Exception var2) {
                logger.error("关闭失败", var2);
            }
        }
    }
}
