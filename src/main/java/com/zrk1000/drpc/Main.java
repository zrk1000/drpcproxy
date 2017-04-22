package com.zrk1000.drpc;

import com.zrk1000.drpc.bolt.DispatchBolt;
import com.zrk1000.drpc.config.ExtendProperties;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.drpc.ReturnResults;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Created by zrk-PC on 2017/4/13.
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            logger.info("load drpcproxy-provider.properties");
            ExtendProperties pps = new ExtendProperties();
            pps.load( Main.class.getClassLoader().getResourceAsStream("drpcproxy-provider.properties"));

            String[] packages = pps.getStringArrayProperty("spring.bean.packages");
            String drpcSpoutName = pps.getProperty("drpc.spout.name");
            String topologyName = pps.getProperty("drpc.topology.name");
            int spoutNum = pps.getIntProperty("drpc.spout.num", 1);
            int dispatchBoltNum = pps.getIntProperty("drpc.dispatch.bolt.num", 1);
            int resultBoltNum = pps.getIntProperty("drpc.result.bolt.num", 1);

            if(!StringUtils.isEmpty(args)&&args.length>0){
                if(args.length==1){
                    drpcSpoutName = args[0];
                    topologyName = args[0];
                }else{
                    drpcSpoutName = args[0];
                    topologyName = args[1];
                }
            }
            logger.info("loading completed ,drpcSpoutName:{},topologyName:{},spoutNum:{},dispatchBoltNum:{},resultBoltNum:{}",
                    drpcSpoutName,topologyName,spoutNum,dispatchBoltNum,resultBoltNum);
            logger.info("submit topology ......");
            TopologyBuilder builder = new TopologyBuilder();
            Config config = new Config();
            DRPCSpout drpcSpout = new DRPCSpout(drpcSpoutName);
            builder.setSpout("drpcSpout", drpcSpout, spoutNum);
            builder.setBolt("dispatch", new DispatchBolt(packages),dispatchBoltNum) .shuffleGrouping("drpcSpout");
            builder.setBolt("return", new ReturnResults(), resultBoltNum).shuffleGrouping("dispatch");
            StormSubmitter.submitTopologyWithProgressBar(topologyName, config, builder.createTopology());
            logger.info("Submit completed ,");
        } catch (AlreadyAliveException e) {
            logger.error("submit failure:{}",e.getMessage());
        } catch (InvalidTopologyException e) {
            logger.error("submit failure:{}",e.getMessage());
        } catch (AuthorizationException e) {
            logger.error("submit failure:{}",e.getMessage());
        } catch (IOException e) {
            logger.error("submit failure:{}",e.getMessage());
        }

    }
}
