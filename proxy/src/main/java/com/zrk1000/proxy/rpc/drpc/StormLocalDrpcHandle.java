package com.zrk1000.proxy.rpc.drpc;

import com.zrk1000.proxy.bolt.BoltHandle;
import com.zrk1000.proxy.bolt.DispatchBolt;
import com.zrk1000.proxy.proxy.ServiceMethod;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.drpc.ReturnResults;
import org.apache.storm.generated.DistributedRPC;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rongkang on 2017-04-02.
 */
public class StormLocalDrpcHandle extends AbsDrpcHandler {

    private static Logger logger = LoggerFactory.getLogger(StormLocalDrpcHandle.class);

    private LocalDRPC drpc ;

    private String drpcService ;

    public StormLocalDrpcHandle(BoltHandle boltHandle) {
        this.drpc = new LocalDRPC();
        this.drpcService = "drpcService";

        TopologyBuilder builder = new TopologyBuilder();
        Config conf = new Config();
        conf.setNumWorkers(2);
        conf.setDebug(true);

        DRPCSpout drpcSpout = new DRPCSpout(this.drpcService, drpc);
        builder.setSpout("drpcSpout", drpcSpout, 1);
        builder.setBolt("dispatch", new DispatchBolt(boltHandle),1) .shuffleGrouping("drpcSpout");
        builder.setBolt("return", new ReturnResults(), 1).shuffleGrouping("dispatch");

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("local_cluster", conf, builder.createTopology());
    }

    @Override
    public DistributedRPC.Iface getDrpcClient() {
        return this.drpc;
    }

    @Override
    public String getDrpcService(ServiceMethod serviceMethod) {
        return this.drpcService;
    }

}
