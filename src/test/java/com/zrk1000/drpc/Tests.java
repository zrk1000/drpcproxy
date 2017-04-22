package com.zrk1000.drpc;

import com.zrk1000.drpc.config.ExtendProperties;

import java.io.IOException;

/**
 * Created by rongkang on 2017-04-22.
 */
public class Tests {

    public static void main(String[] args) {
        try {
            ExtendProperties pps = new ExtendProperties();
            pps.load(Main.class.getClassLoader().getResourceAsStream("drpcproxy-provider.properties"));
            String[] property = pps.getStringArrayProperty("spring.bean.packages");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
