package com.example.calltest.kit;

/**
 * Created by 14069 on 2020/3/10.
 */
public class CloseKit {

    private CloseKit() {
    }

    public static void close(AutoCloseable s) {
        if(s != null) {
            try {
                s.close();
                s = null;
            } catch (Exception var2) {
                ;
            }
        }

    }
}
