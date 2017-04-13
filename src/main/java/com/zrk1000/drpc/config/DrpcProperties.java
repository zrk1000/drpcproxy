package com.zrk1000.drpc.config;

import java.util.Properties;

/**
 * Created by zrk-PC on 2017/4/13.
 */
public class DrpcProperties extends Properties {


    public int getIntProperty(String key) {
        String val = getProperty(key);
        if(val == null){
            throw  new RuntimeException(" property \""+key+"\"'s  value should be not null");
        }
        return Integer.valueOf(val);
    }

    public int getIntProperty(String key, int defaultValue) {
        String val = getProperty(key);
        return (val == null) ?  defaultValue : Integer.valueOf(val);
    }

    public double getDoubleProperty(String key) {
        String val = getProperty(key);
        if(val == null){
            throw  new RuntimeException(" property \""+key+"\"'s  value should be not null");
        }
        return Double.valueOf(val);
    }

    public double getDoubleProperty(String key, double defaultValue) {
        String val = getProperty(key);
        return (val == null) ?  defaultValue : Double.valueOf(val);
    }

    public String[] getStringArrayProperty(String key) {
        String val = getProperty(key);
        return (val == null) ?  val.split(",") : new String[]{};
    }

    public String[] getStringArrayProperty(String key,String separator) {
        String val = getProperty(key);
        return (val == null) ?  val.split(separator) : new String[]{};
    }


}
