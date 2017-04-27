package com.zrk1000.demo;

import com.zrk1000.proxy.ConfigMain;
import com.zrk1000.proxy.bolt.ConfigDispatchBolt;
import com.zrk1000.proxy.config.ExtendProperties;
import com.zrk1000.proxy.proxy.ServiceProxyFactory;
import com.zrk1000.proxy.rpc.RpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormLocalDrpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormRemoteDrpcHandle;
import org.apache.storm.Config;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/4/27
 * Time: 15:27
 */
public class ServiceImplFactory {

    private static RpcHandle rpcHandle = null;

    static {
        try {
            ExtendProperties pps = new ExtendProperties();
            pps.load( ConfigMain.class.getClassLoader().getResourceAsStream("drpcproxy-consumer.properties"));

            String drpcPattern = pps.getProperty("drpc.pattern");

            Map<String, String> drpcClientConfig = pps.getSubProperty("drpc.client.config.", true);
            Config config = new Config();
            config.putAll(drpcClientConfig);

            String host = pps.getProperty("drpc.client.host", "localhost");
            int port = pps.getIntProperty("drpc.client.port", 3772);
            int stormTimeout = pps.getIntProperty("drpc.client.timeout", 3000);
            Map<String, Set<String>> drpcServiceMap = pps.getSubPropertyValueToSet("topology.mapping.config.", true);

            if("remote".equals(drpcPattern)){
                rpcHandle = new StormRemoteDrpcHandle(config,host, port, stormTimeout, drpcServiceMap);
            }else {
                rpcHandle = new StormLocalDrpcHandle(ConfigDispatchBolt.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static <T> T newInstance(Class<T> clazz) {
        return  ServiceProxyFactory.newInstance(clazz, rpcHandle);
    }



}

    