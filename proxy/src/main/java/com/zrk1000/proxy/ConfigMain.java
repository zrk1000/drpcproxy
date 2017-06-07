package com.zrk1000.proxy;

import com.zrk1000.proxy.bolt.BoltHandle;
import com.zrk1000.proxy.bolt.DispatchBolt;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by zrk-PC on 2017/4/13.
 */
public class ConfigMain {

    private static Logger logger = LoggerFactory.getLogger(ConfigMain.class);

    public static void main(String[] args) {

        try {
            ExtendProperties pps = new ExtendProperties();
            pps.load( ConfigMain.class.getClassLoader().getResourceAsStream("drpcproxy-provider.properties"));

            String[] serviceImpls = pps.getStringArrayProperty("service.impls");

            String drpcSpoutName = pps.getProperty("drpc.spout.name");
            String drpcBoltHandle = pps.getProperty("drpc.bolt.handle","com.zrk1000.proxy.bolt.ConfigBoltHandle");
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
            Class cls=Class.forName(drpcBoltHandle);  //import class
            Constructor cst=cls.getConstructor(Collection.class); //get the constructor
            BoltHandle boltHandle=(BoltHandle)cst.newInstance(Arrays.asList(serviceImpls)); //get a new instance

            DispatchBolt dispatchBolt = new DispatchBolt(boltHandle);

            TopologyBuilder builder = new TopologyBuilder();
            Config config = new Config();
            DRPCSpout drpcSpout = new DRPCSpout(drpcSpoutName);
            builder.setSpout("drpcSpout", drpcSpout, spoutNum);
            builder.setBolt("dispatch",dispatchBolt ,dispatchBoltNum) .shuffleGrouping("drpcSpout");
            builder.setBolt("return", new ReturnResults(), resultBoltNum).shuffleGrouping("dispatch");
            StormSubmitter.submitTopologyWithProgressBar(topologyName, config, builder.createTopology());
        } catch (AlreadyAliveException e) {
            logger.error(e.getMessage());
        } catch (InvalidTopologyException e) {
            logger.error(e.getMessage());
        } catch (AuthorizationException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        } catch (InstantiationException e) {
            logger.error(e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage());
        }

    }
}
