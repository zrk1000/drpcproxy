package com.zrk1000.proxy;

import com.zrk1000.proxy.bolt.BoltHandle;
import com.zrk1000.proxy.bolt.ConfigBoltHandle;
import com.zrk1000.proxy.config.ExtendProperties;
import com.zrk1000.proxy.proxy.ServiceProxyFactory;
import com.zrk1000.proxy.rpc.RpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormLocalDrpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormRemoteDrpcHandle;
import org.apache.storm.Config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于配置文件配置的服务提供-消费模式的service工厂类
 * User: zrk-PC
 * Date: 2017/4/27
 * Time: 15:27
 */
public class ServiceImplFactory {

    public enum DrpcPattern{
        REMOTE,LOCAL,RELY
    }

    private static RpcHandle rpcHandle = null;
    private static String drpcPattern = null;

    private static Map<String,Class<?>> serviceImplsMap = new ConcurrentHashMap();

    public static void init(){
        try {
            ExtendProperties pps = new ExtendProperties();
            pps.load( ConfigMain.class.getClassLoader().getResourceAsStream("drpcproxy-consumer.properties"));
            drpcPattern = pps.getProperty("drpc.pattern");
            String drpcBoltHandle = pps.getProperty("drpc.bolt.handle","com.zrk1000.proxy.bolt.ConfigBoltHandle");

            if(DrpcPattern.REMOTE.toString().equalsIgnoreCase(drpcPattern)){
                //获取Config配置
                Map<String, String> drpcClientConfig = pps.getSubProperty("drpc.client.config.", true);
                Config config = new Config();
                config.putAll(drpcClientConfig);
                //获取host port timeout配置
                String host = pps.getProperty("drpc.client.host", "localhost");
                int port = pps.getIntProperty("drpc.client.port", 3772);
                int stormTimeout = pps.getIntProperty("drpc.client.timeout", 3000);
                //获取drpcService（拓扑）提供的service服务映射集
                Map<String, Set<String>> drpcServiceMap = pps.getSubPropertyValueToSet("topology.mapping.config.", true);
                rpcHandle = new StormRemoteDrpcHandle(config,host, port, stormTimeout, drpcServiceMap);
            }else if(DrpcPattern.LOCAL.toString().equalsIgnoreCase(drpcPattern)){
                //获取消费方service服务列表
                Set<String> serviceImpls = loadServiceImpls();
                Class cls=Class.forName(drpcBoltHandle);  //import class
                Constructor cst=cls.getConstructor(Collection.class); //get the constructor
                BoltHandle boltHandle=(BoltHandle)cst.newInstance(Arrays.asList(serviceImpls)); //get a new instance
                rpcHandle = new StormLocalDrpcHandle(new ConfigBoltHandle(serviceImpls));
            }else if(DrpcPattern.RELY.toString().equalsIgnoreCase(drpcPattern)){
                //获取消费方service服务列表
                Set<String> serviceImpls = loadServiceImpls();
                for (String serviceImpl : serviceImpls){
                    try {
                        Class<?> serviceImplClass = Class.forName(serviceImpl);
                        for (Class<?> _interface:serviceImplClass.getInterfaces() )
                            serviceImplsMap.put(_interface.getCanonicalName(),serviceImplClass);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Unable to find the class \""+serviceImpl+"\"");
                    }
                }
            }else{
                throw new RuntimeException("The drpc.pattern parameter cannot be empty");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public static <T> T newInstance(Class<T> clazz) {
        if(DrpcPattern.RELY.toString().equalsIgnoreCase(drpcPattern)){
            Class<?> aClass = serviceImplsMap.get(clazz.getCanonicalName());
            T impl = null;
            try {
                impl = (T)aClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return impl;
        }
        return  ServiceProxyFactory.newInstance(clazz, rpcHandle);
    }

    //获取消费方service服务列表
    public static Set<String> loadServiceImpls() throws IOException{
        Set<String> serviceImpls = new HashSet<String>();
        Enumeration<URL> resources = ServiceImplFactory.class.getClassLoader().getResources("drpcproxy-provider.properties");
        while(resources.hasMoreElements()) {
            InputStream in = null;
            try {
                in = resources.nextElement().openStream();
                ExtendProperties eps = new ExtendProperties();
                eps.load(in);
                String[] arrayProperty = eps.getStringArrayProperty("service.impls");
                for(String property:arrayProperty){
                    if(serviceImpls.contains(property))
                        throw new RuntimeException("The 'drpcproxy-provider.properties' contains the same '"+ property +"' content");
                    serviceImpls.add(property);
                }
            } finally {
                if(in != null)
                    try {in.close();} catch (IOException e) {e.printStackTrace();}
            }
        }
        if(serviceImpls.size()==0)
            throw new RuntimeException("The 'drpcproxy-provider.properties' serviceImpls are empty");
        return serviceImpls;
    }

}

    