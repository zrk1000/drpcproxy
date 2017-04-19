package com.zrk1000.drpc.rpc.drpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zrk1000.drpc.proxy.ServiceMethod;
import com.zrk1000.drpc.rpc.RpcHandle;
import org.apache.storm.shade.com.google.common.collect.Maps;
import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;

import java.io.IOException;
import java.util.Map;

/**
 * Created by rongkang on 2017-04-02.
 */
//@Profile("prod")
//@Component("stormRemoteDrpcHandle")
public class StormRemoteDrpcHandle implements RpcHandle {

    private DRPCClient drpc ;

    private String drpcService ;

    public StormRemoteDrpcHandle() {
        Map<String,Object> map = Maps.newHashMap();
        map.put("storm.thrift.transport","org.apache.storm.security.auth.SimpleTransportPlugin");
        map.put("storm.nimbus.retry.times",3);
        map.put("storm.nimbus.retry.interval.millis",10000);
        map.put("storm.nimbus.retry.intervalceiling.millis",60000);
        map.put("drpc.max_buffer_size",104857600);
        try {
            this.drpc = new DRPCClient(map,"192.168.1.81",3772,3000);
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        this.drpcService = "drpcService";

    }

    public Object exec(ServiceMethod serviceMethod, Object[] args) {
        DrpcResponse drpcResponse = new DrpcResponse();
        String result = null;
        try{
            String drpcService = "";

            result = drpc.execute(this.drpcService, new DrpcRequest(serviceMethod.getClazz(),serviceMethod.getMethodName(),serviceMethod.hashCode(),args).toJSONString());
        }catch (Exception e){
            e.printStackTrace();
            drpcResponse.setCode(500);
            drpcResponse.setMsg(e.getMessage());
        }
        if(result!=null)
            drpcResponse = JSON.parseObject(result, DrpcResponse.class);
        return JSONObject.parseObject(JSON.toJSONString(drpcResponse.getData()), serviceMethod.getReturnType());
    }

    public void close() throws IOException {
        System.out.println("==============do close!");
    }
}
