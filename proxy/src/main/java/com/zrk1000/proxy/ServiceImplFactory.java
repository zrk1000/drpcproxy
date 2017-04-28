package com.zrk1000.proxy;

import com.zrk1000.proxy.bolt.ConfigDispatchBolt;
import com.zrk1000.proxy.config.ExtendProperties;
import com.zrk1000.proxy.proxy.ServiceProxyFactory;
import com.zrk1000.proxy.rpc.RpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormLocalDrpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormRemoteDrpcHandle;
import org.apache.storm.Config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/4/27
 * Time: 15:27
 */
public class ServiceImplFactory {

    private static RpcHandle rpcHandle = null;
    private static String drpcPattern = null;

    private static Map<String,Class<?>> serviceImplsMap = new ConcurrentHashMap();

    public static void init(){
        try {
            ExtendProperties pps = new ExtendProperties();
            pps.load( ConfigMain.class.getClassLoader().getResourceAsStream("drpcproxy-consumer.properties"));

            drpcPattern = pps.getProperty("drpc.pattern");

            Map<String, String> drpcClientConfig = pps.getSubProperty("drpc.client.config.", true);
            Config config = new Config();
            config.putAll(drpcClientConfig);

            String host = pps.getProperty("drpc.client.host", "localhost");
            int port = pps.getIntProperty("drpc.client.port", 3772);
            int stormTimeout = pps.getIntProperty("drpc.client.timeout", 3000);
            Map<String, Set<String>> drpcServiceMap = pps.getSubPropertyValueToSet("topology.mapping.config.", true);

            if("remote".equals(drpcPattern)){
                rpcHandle = new StormRemoteDrpcHandle(config,host, port, stormTimeout, drpcServiceMap);
            }else if("local".equals(drpcPattern)){
                rpcHandle = new StormLocalDrpcHandle(ConfigDispatchBolt.class);
            }else if("rely".equals(drpcPattern)){
                Set<String> serviceImpls = new HashSet<String>();
                Enumeration<URL> resources = StormLocalDrpcHandle.class.getClassLoader().getResources("drpcproxy-provider.properties");
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

                for (String serviceImpl : serviceImpls){
                    try {
                        Class<?> serviceImplClass = Class.forName(serviceImpl);
                        for (Class<?> _interface:serviceImplClass.getInterfaces() )
                            serviceImplsMap.put(_interface.getCanonicalName(),serviceImplClass);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Unable to find the class \""+serviceImpl+"\"");
                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static <T> T newInstance(Class<T> clazz) {
        if("rely".equals(drpcPattern)){
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



}

    