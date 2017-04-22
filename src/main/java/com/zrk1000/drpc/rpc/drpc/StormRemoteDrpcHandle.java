package com.zrk1000.drpc.rpc.drpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zrk1000.drpc.proxy.ServiceMethod;
import com.zrk1000.drpc.rpc.RpcHandle;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.storm.Config;
import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by rongkang on 2017-04-02.
 */
public class StormRemoteDrpcHandle implements RpcHandle {

    private static Logger logger = LoggerFactory.getLogger(StormRemoteDrpcHandle.class);

    private static GenericObjectPool<DRPCClient> drpcClientPool = null;

    private static Map<String,Set<String>> drpcServiceMap  = null ;

    public StormRemoteDrpcHandle(Config conf, String stormHost, Integer stormPort,Integer stormTimeout,GenericObjectPoolConfig poolConfig,Map<String,Set<String>> drpcServiceMap) {
        DrpcClientFactory factory = new DrpcClientFactory(conf, stormHost, stormPort, stormTimeout);
        if(drpcClientPool!=null)
            return;
        this.drpcClientPool = new GenericObjectPool<DRPCClient>(factory,poolConfig);
        this.drpcServiceMap = drpcServiceMap;
    }

    public Object exec(ServiceMethod serviceMethod, Object[] args) {
        DRPCClient client = null;
        String result = null;
        DrpcResponse drpcResponse = new DrpcResponse();
        try{
            client = drpcClientPool.borrowObject();
            String drpcService = getDrpcService(serviceMethod.getClazz());
            if(drpcService == null)
                throw  new RuntimeException("Did not match to the remote DRPC, please check the configuration");
            result = client.execute(drpcService, new DrpcRequest(serviceMethod.getClazz(),serviceMethod.getMethodName(),serviceMethod.hashCode(),args).toJSONString());
            if(result!=null)
                drpcResponse = JSON.parseObject(result, DrpcResponse.class);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            drpcResponse.setCode(500);
            drpcResponse.setMsg(e.getMessage());
        }finally {
            drpcClientPool.returnObject(client);
        }

        return JSONObject.parseObject(JSON.toJSONString(drpcResponse.getData()), serviceMethod.getReturnType());
    }

    public String getDrpcService(String clazz) {
        for(String key:drpcServiceMap.keySet())
            if(drpcServiceMap.get(key).contains(clazz))
                return key;
        return null;
    }

    public void close() throws IOException {
//        drpcClientPool.close();
        logger.info("drpcClientPool  close!");
    }


}
