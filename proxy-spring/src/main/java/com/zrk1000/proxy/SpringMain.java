package com.zrk1000.proxy;

import com.zrk1000.proxy.bolt.DispatchBolt;
import com.zrk1000.proxy.bolt.SpringBoltHandle;
import com.zrk1000.proxy.config.ExtendProperties;
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

import java.io.IOException;

/**
 * Created by zrk-PC on 2017/4/13.
 */
public class SpringMain {

    private static Logger logger = LoggerFactory.getLogger(ConfigMain.class);

    public static void main(String[] args) {

        try {
            logger.info("加载配置文件......");
            ExtendProperties pps = new ExtendProperties();
            pps.load( SpringMain.class.getClassLoader().getResourceAsStream("drpcproxy-provider.properties"));

            String[] packages = pps.getStringArrayProperty("service.impls");
            String drpcSpoutName = pps.getProperty("drpc.spout.name");
            String topologyName = pps.getProperty("drpc.topology.name");
            int spoutNum = pps.getIntProperty("drpc.spout.num", 1);
            int dispatchBoltNum = pps.getIntProperty("drpc.dispatch.bolt.num", 1);
            int resultBoltNum = pps.getIntProperty("drpc.result.bolt.num", 1);

            if(args!=null&&args.length>0){
                if(args.length==1){
                    drpcSpoutName = args[0];
                    topologyName = args[0];
                }else{
                    drpcSpoutName = args[0];
                    topologyName = args[1];
                }
            }
            logger.info("配置文件加载完成，packages：{}，drpcSpoutName：{}，topologyName：{}，spoutNum：{}，dispatchBoltNum：{}，resultBoltNum：{}",
                    packages,drpcSpoutName,topologyName,spoutNum,dispatchBoltNum,resultBoltNum);
            logger.info("构建topology");

            DispatchBolt dispatchBolt = new DispatchBolt(new SpringBoltHandle(packages));

            TopologyBuilder builder = new TopologyBuilder();
            Config config = new Config();
            DRPCSpout drpcSpout = new DRPCSpout(drpcSpoutName);
            builder.setSpout("drpcSpout", drpcSpout, spoutNum);
            builder.setBolt("dispatch", dispatchBolt ,dispatchBoltNum) .shuffleGrouping("drpcSpout");
            builder.setBolt("return", new ReturnResults(), resultBoltNum).shuffleGrouping("dispatch");
            logger.info("构建topology完毕，提交jar");
            StormSubmitter.submitTopologyWithProgressBar(topologyName, config, builder.createTopology());
            logger.info("提交jar完成");
        } catch (AlreadyAliveException e) {
            logger.error(e.getMessage());
        } catch (InvalidTopologyException e) {
            logger.error(e.getMessage());
        } catch (AuthorizationException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }
}
