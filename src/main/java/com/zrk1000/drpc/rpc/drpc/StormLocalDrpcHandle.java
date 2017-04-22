package com.zrk1000.drpc.rpc.drpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zrk1000.drpc.bolt.DispatchBolt;
import com.zrk1000.drpc.config.ExtendProperties;
import com.zrk1000.drpc.proxy.ServiceMethod;
import com.zrk1000.drpc.rpc.RpcHandle;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.drpc.ReturnResults;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Timer;

/**
 * Created by rongkang on 2017-04-02.
 */
public class StormLocalDrpcHandle implements RpcHandle {

    private static Logger logger = LoggerFactory.getLogger(StormLocalDrpcHandle.class);

    private static LocalDRPC drpc ;

    private static String drpcService ;

    private static LocalCluster cluster ;

    public StormLocalDrpcHandle() {
        if(drpc!=null)
            return;
        this.drpc = new LocalDRPC();
        this.drpcService = "drpcService";

        TopologyBuilder builder = new TopologyBuilder();
        Config conf = new Config();
        conf.setNumWorkers(1);
        conf.setDebug(true);

        ExtendProperties pps = new ExtendProperties();
        try {
            pps.load( StormLocalDrpcHandle.class.getClassLoader().getResourceAsStream("drpcproxy-provider.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] packages = pps.getStringArrayProperty("spring.bean.packages");
        DRPCSpout drpcSpout = new DRPCSpout(this.drpcService, drpc);
        builder.setSpout("drpcSpout", drpcSpout, 1);
        builder.setBolt("dispatch", new DispatchBolt(packages),1) .shuffleGrouping("drpcSpout");
        builder.setBolt("return", new ReturnResults(), 1).shuffleGrouping("dispatch");
        cluster = new LocalCluster();
        cluster.submitTopology("local_cluster", conf, builder.createTopology());
    }

    public Object exec(ServiceMethod serviceMethod, Object[] args) {
        DrpcResponse drpcResponse = new DrpcResponse();
        String result = null;
        try{
            result = drpc.execute(this.drpcService, new DrpcRequest(serviceMethod.getClazz(),serviceMethod.getMethodName(),serviceMethod.hashCode(),args).toJSONString());
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            drpcResponse.setCode(500);
            drpcResponse.setMsg(e.getMessage());
        }
        if(result!=null)
            drpcResponse = JSON.parseObject(result, DrpcResponse.class);

        return JSONObject.parseObject(JSON.toJSONString(drpcResponse.getData()), serviceMethod.getReturnType());
    }

    public void close() throws IOException {
//        if(drpc!=null)
//            drpc.clone();
//        if(cluster!=null)
//            cluster.shutdown();
        logger.info("LocalCluster  shutdown!");
    }
}
