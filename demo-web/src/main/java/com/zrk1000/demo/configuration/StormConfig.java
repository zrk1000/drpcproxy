package com.zrk1000.demo.configuration;


import com.zrk1000.proxy.annotation.ServiceScan;
import com.zrk1000.proxy.bolt.SpringDispatchBolt;
import com.zrk1000.proxy.rpc.RpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormLocalDrpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormRemoteDrpcHandle;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.storm.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.Set;

/**
 * Created by rongkang on 2017-04-20.
 */
@Profile({"local","remote"})
@Configuration
@ServiceScan(basePackages = "com.zrk1000.demo.service",rpcHandleBeanRef="stormDrpcHandle")
public class StormConfig {

    @Profile("local")
    @Scope("singleton")
    @Bean("stormDrpcHandle")
    public RpcHandle getStormLocalRpcHandle(){
        StormLocalDrpcHandle drpcHandle = new StormLocalDrpcHandle(new SpringDispatchBolt());
        return  drpcHandle;
    }

    @Bean
    @ConfigurationProperties("pool.config")
    public GenericObjectPoolConfig getGenericObjectPoolConfig(){
        return new GenericObjectPoolConfig();
    }

    @Bean
    @ConfigurationProperties("drpc.client")
    public DrpcClientConfig getDrpcClientConfig(){
        return new DrpcClientConfig();
    }

    @Bean
    @ConfigurationProperties("spout.mapping")
    public SpoutMapping getSpoutMapping(){
        return new SpoutMapping();
    }





    @Profile("remote")
//    @Scope("singleton")
    @Bean("stormDrpcHandle")
    public RpcHandle getStormRemoteRpcHandle(GenericObjectPoolConfig poolConfig,DrpcClientConfig clientConfig,SpoutMapping spoutMapping){
        Config config = new Config();
        config.putAll(clientConfig.getConfig());
        return  new StormRemoteDrpcHandle(config,clientConfig.getHost(),clientConfig.getPort(),clientConfig.getTimeout(),poolConfig,spoutMapping.getConfig());
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

    class SpoutMapping{

        Map<String, Set<String>> config;

        public Map<String, Set<String>> getConfig() {
            return config;
        }

        public void setConfig(Map<String, Set<String>> config) {
            this.config = config;
        }
    }

}
