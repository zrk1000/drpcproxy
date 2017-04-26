package com.zrk1000.proxy.rpc.drpc;


import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.storm.Config;
import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Created by rongkang on 2017-04-20.
 */
public class DrpcClientFactory extends BasePooledObjectFactory<DRPCClient> {

    private final static Logger logger = LoggerFactory.getLogger(DrpcClientFactory.class);

    private Config config;
    private  String stormHost;
    private  Integer stormPort;
    private  Integer stormTimeout;

    public DrpcClientFactory(Config conf, String stormHost, Integer stormPort,Integer stormTimeout) {
        this.config = conf;
        this.stormHost = stormHost;
        this.stormPort = stormPort;
        this.stormTimeout = stormTimeout;
    }

    public DRPCClient create() throws Exception {
        return new DRPCClient(config,stormHost,stormPort,stormTimeout);
    }

    public PooledObject<DRPCClient> wrap(DRPCClient obj) {
        return new DefaultPooledObject<DRPCClient>(obj);
    }
}
