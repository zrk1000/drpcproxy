package com.zrk1000.drpc.rpc.drpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zrk1000.drpc.Main;
import com.zrk1000.drpc.config.ExtendProperties;
import com.zrk1000.drpc.proxy.ServiceMethod;
import com.zrk1000.drpc.rpc.RpcHandle;
import org.apache.storm.Config;
import org.apache.storm.shade.com.google.common.collect.Maps;
import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rongkang on 2017-04-02.
 */
//@Profile("prod")
//@Component("stormRemoteDrpcHandle")
public class StormRemoteDrpcHandle implements RpcHandle {

    private DRPCClient drpc ;

    private Map<String,Set<String>> drpcServiceMap ;

    public StormRemoteDrpcHandle() {
        try {
            Config config = new Config();
            ExtendProperties pps = new ExtendProperties();
            pps.load( Main.class.getResourceAsStream("classpath*:drpcproxy-consumer.properties"));
            config.putAll(pps.getSubProperty("drpcproxy.",true));
            String stormHost = pps.getProperty("strom.host");
            Integer stormPort = pps.getIntProperty("strom.port");
            Integer stormTimeout = pps.getIntProperty("strom.timeout");

            this.drpc = new DRPCClient(pps,stormHost,stormPort,stormTimeout);
            this.drpcServiceMap = pps.getSubPropertyValueToSet("drpcspout.", true);
        } catch (TTransportException e) {
            e.printStackTrace();
            throw new RuntimeException("初始化 StormRemoteDrpcHandle 失败,请检查配置");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("初始化 StormRemoteDrpcHandle 失败 ,读取配置文件错误");
        }

    }

    public Object exec(ServiceMethod serviceMethod, Object[] args) {
        DrpcResponse drpcResponse = new DrpcResponse();
        String result = null;
        try{
            String drpcService = getDrpcService(serviceMethod.getClazz());
            if(drpcResponse == null)
                throw  new RuntimeException("未匹配到的远程drpc，请检查配置");
            result = drpc.execute(drpcService, new DrpcRequest(serviceMethod.getClazz(),serviceMethod.getMethodName(),serviceMethod.hashCode(),args).toJSONString());
            if(result!=null)
                drpcResponse = JSON.parseObject(result, DrpcResponse.class);
        }catch (Exception e){
            e.printStackTrace();
            drpcResponse.setCode(500);
            drpcResponse.setMsg(e.getMessage());
        }

        return JSONObject.parseObject(JSON.toJSONString(drpcResponse.getData()), serviceMethod.getReturnType());
    }

    public void close() throws IOException {
        System.out.println("==============do close!");
    }

    public String getDrpcService(String clazz) {
        for(String key:drpcServiceMap.keySet()){
            if(drpcServiceMap.get(key).contains(clazz))
                return key;
        }
        return null;
    }
}
