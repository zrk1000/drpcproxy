package com.zrk1000.proxy.rpc.drpc;

import com.zrk1000.proxy.proxy.ServiceMethod;
import org.apache.storm.Config;
import org.apache.storm.generated.DistributedRPC;
import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Created by rongkang on 2017-04-02.
 */
public class StormRemoteDrpcHandle  extends AbsDrpcHandler {

    private static Logger logger = LoggerFactory.getLogger(StormRemoteDrpcHandle.class);

    private static Map<String,Set<String>> drpcServiceMap  = null ;

    private static String stormHost;
    private static int stormPort;
    private static int stormTimeout;
    private static Config conf;

    public StormRemoteDrpcHandle(Config conf, String stormHost, Integer stormPort, Integer stormTimeout, Map<String,Set<String>> drpcServiceMap) {
        this.conf = conf;
        this.stormHost = stormHost;
        this.stormPort = stormPort;
        this.stormTimeout = stormTimeout;
        this.drpcServiceMap = drpcServiceMap;
    }

    @Override
    public DistributedRPC.Iface getDrpcClient() throws Throwable {
        return new DRPCClient(conf, stormHost, stormPort, stormTimeout);
    }

    @Override
    public String getDrpcService(ServiceMethod serviceMethod) {
        String drpcService = getDrpcService(serviceMethod.getClazz());
        if(drpcService == null)
            throw  new RuntimeException("Did not match to the remote DRPC, please check the configuration");
        return drpcService;
    }

    @Override
    public void postProcess(DistributedRPC.Iface drpcClient){
        if(drpcClient!=null && drpcClient instanceof DRPCClient)
            ((DRPCClient) drpcClient).close();
    }


    public String getDrpcService(String clazz) {
        for(String key:drpcServiceMap.keySet())
            if(drpcServiceMap.get(key).contains(clazz))
                return key;
        return null;
    }

}

