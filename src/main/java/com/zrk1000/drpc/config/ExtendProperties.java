package com.zrk1000.drpc.config;

import java.util.*;

import static java.io.File.separator;

/**
 * Created by zrk-PC on 2017/4/13.
 */
public class ExtendProperties extends Properties {


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
        return (val == null) ?  new String[]{}: val.split(",") ;
    }

    public String[] getStringArrayProperty(String key,String separator) {
        String val = getProperty(key);
        return (val == null) ?   new String[]{}:val.split(separator) ;
    }

    public Map<String ,String > getSubProperty(String prefix,boolean removeThePrefix) {
        Map<String ,String > map = new HashMap<String, String>();
        Set<Object> set = this.keySet();
        for (Object key : set)
            if(key.toString().startsWith(prefix))
                map.put(removeThePrefix?key.toString().substring(prefix.length()):key.toString(),
                        this.get(key)!=null?this.get(key).toString():"");
        return  map;
    }

    public Map<String ,Set<String> > getSubPropertyValueToSet(String prefix,boolean removeThePrefix) {
        Map<String ,Set<String> >  result = new HashMap<String, Set<String>>();
        Map<String, String> subProperty = getSubProperty(prefix, removeThePrefix);
        for (String key :subProperty.keySet()){
            String vaule = subProperty.get(key);
            String[] split = vaule.split(",");
            result.put(key,new HashSet<String>(Arrays.asList(split)));
        }
        return  result;
    }

}
