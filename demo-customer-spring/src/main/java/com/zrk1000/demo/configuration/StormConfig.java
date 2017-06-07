package com.zrk1000.demo.configuration;


import com.zrk1000.proxy.ServiceImplFactory;
import com.zrk1000.proxy.annotation.ServiceScan;
import com.zrk1000.proxy.bolt.SpringBoltHandle;
import com.zrk1000.proxy.rpc.RpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormLocalDrpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormRemoteDrpcHandle;
import org.apache.storm.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by rongkang on 2017-04-20.
 */
@Profile({"local","remote"})
@Configuration
@ServiceScan(basePackages = "com.zrk1000.demo.service",
//        excludeClasses = {UserService.class},
        rpcHandleBeanRef="stormDrpcHandle")
public class StormConfig {

    @Profile("local")
    @Scope("singleton")
    @Bean("stormDrpcHandle")
    public RpcHandle getStormLocalRpcHandle(){
        StormLocalDrpcHandle drpcHandle = null;
        try {
            Set<String> serviceImpls = ServiceImplFactory.loadServiceImpls();
            SpringBoltHandle springBoltHandle = new SpringBoltHandle(serviceImpls.toArray(new String[serviceImpls.size()]));
            drpcHandle = new StormLocalDrpcHandle(springBoltHandle);
        } catch (IOException e) {
            throw new RuntimeException("初始化stormDrpcHandle失败");
        }
        return  drpcHandle;
    }

    @Bean
    @ConfigurationProperties("drpc.client")
    public DrpcClientConfig getDrpcClientConfig(){
        return new DrpcClientConfig();
    }

    @Bean
    @ConfigurationProperties("topology.mapping")
    public TopologyMapping getTopologyMapping(){
        return new TopologyMapping();
    }


    @Profile("remote")
    @Bean("stormDrpcHandle")
    public RpcHandle getStormRemoteRpcHandle(DrpcClientConfig clientConfig,TopologyMapping topologyMapping){
        Config config = new Config();
        config.putAll(clientConfig.getConfig());
        return  new StormRemoteDrpcHandle(config,clientConfig.getHost(),clientConfig.getPort(),clientConfig.getTimeout(),topologyMapping.getConfig());
    }


    class DrpcClientConfig{
        private String host;
        private Integer port;
        private Integer timeout;
        private Map<String,String> config ;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getTimeout() {
            return timeout;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }

        public Map<String, String> getConfig() {
            return config;
        }

        public void setConfig(Map<String, String> config) {
            this.config = config;
        }
    }

    class TopologyMapping {

        Map<String, Set<String>> config;

        public Map<String, Set<String>> getConfig() {
            return config;
        }

        public void setConfig(Map<String, Set<String>> config) {
            this.config = config;
        }
    }

}
