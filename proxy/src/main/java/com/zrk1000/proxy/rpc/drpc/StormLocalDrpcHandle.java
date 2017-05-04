package com.zrk1000.proxy.rpc.drpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zrk1000.proxy.bolt.AbsDispatchBolt;
import com.zrk1000.proxy.bolt.ConfigDispatchBolt;
import com.zrk1000.proxy.config.ExtendProperties;
import com.zrk1000.proxy.proxy.ServiceMethod;
import com.zrk1000.proxy.rpc.RpcHandle;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.drpc.ReturnResults;
import org.apache.storm.shade.com.google.common.collect.Sets;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;

/**
 * Created by rongkang on 2017-04-02.
 */
public class StormLocalDrpcHandle implements RpcHandle {

    private static Logger logger = LoggerFactory.getLogger(StormLocalDrpcHandle.class);

    private LocalDRPC drpc ;

    private String drpcService ;

    public StormLocalDrpcHandle(Class<? extends AbsDispatchBolt> clazz) {
        AbsDispatchBolt dispatchBolt = null;
        try {
            dispatchBolt = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.drpc = new LocalDRPC();
        this.drpcService = "drpcService";

        TopologyBuilder builder = new TopologyBuilder();
        Config conf = new Config();
        conf.setNumWorkers(2);
        conf.setDebug(true);
//        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);


        Set<String> serviceImpls = new HashSet<String>();
        try {
            Enumeration<URL> resources = StormLocalDrpcHandle.class.getClassLoader().getResources("drpcproxy-provider.properties");
            while(resources.hasMoreElements()) {
                InputStream in = null;
                try {
                    in = resources.nextElement().openStream();
                    ExtendProperties eps = new ExtendProperties();
                    eps.load(in);
                    String[] arrayProperty = eps.getStringArrayProperty("service.impls");
                    for(String property:arrayProperty){
                        if(serviceImpls.contains(property))
                            throw new RuntimeException("The 'drpcproxy-provider.properties' contains the same '"+ property +"' content");
                        serviceImpls.add(property);
                    }

                } finally {
                    if(in != null)
                        try {in.close();} catch (IOException e) {e.printStackTrace();}
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading 'drpcproxy-provider.properties'");
        }

        dispatchBolt.init(serviceImpls.toArray(new String[serviceImpls.size()]));

        DRPCSpout drpcSpout = new DRPCSpout(this.drpcService, drpc);
        builder.setSpout("drpcSpout", drpcSpout, 1);
        builder.setBolt("dispatch", dispatchBolt,1) .shuffleGrouping("drpcSpout");
        builder.setBolt("return", new ReturnResults(), 1).shuffleGrouping("dispatch");

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("local_cluster", conf, builder.createTopology());
    }

    public Object exec(ServiceMethod serviceMethod, Object[] args) throws Throwable {
        DrpcResponse drpcResponse = new DrpcResponse();
        String result = null;
//        try{
            result = drpc.execute(this.drpcService, new DrpcRequest(serviceMethod.getClazz(),serviceMethod.getMethodName(),serviceMethod.hashCode(),args).toJSONString());
//        }catch (Exception e){
//            logger.error(e.getMessage(),e);
//            drpcResponse.setCode(500);
//            drpcResponse.setMsg(e.getMessage());
//        }
        if(logger.isDebugEnabled())
            logger.debug("The remote invocation returns the result:"+ result);
        if(result!=null)
            drpcResponse = JSON.parseObject(result, DrpcResponse.class);

        if (drpcResponse.hasException()) {
            try {
                Throwable exception = drpcResponse.getException();

                // 如果是checked异常，直接抛出
                if (! (exception instanceof RuntimeException) && (exception instanceof Exception)) {
                    throw exception;
                }
                // 在方法签名上有声明，直接抛出
                Class<?>[] exceptionClassses = serviceMethod.getExceptionClassses();
                for (Class<?> exceptionClass : exceptionClassses) {
                    if (exception.getClass().equals(exceptionClass)) {
                        throw exception;
                    }
                }
                // 异常类和接口类在同一jar包里，直接抛出
                String serviceFile = getCodeBase(serviceMethod.getServiceInterface());
                String exceptionFile = getCodeBase(exception.getClass());
                if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)){
                    throw exception;
                }
                // 是JDK自带的异常，直接抛出
                String className = exception.getClass().getName();
                if (className.startsWith("java.") || className.startsWith("javax.")) {
                    throw exception;
                }
                // 否则，包装成RuntimeException抛给客户端
                throw new RuntimeException(exception.getMessage());
            } catch (Exception e) {
                throw e;
            }
        }

        return JSONObject.parseObject(JSON.toJSONString(drpcResponse.getData()), serviceMethod.getReturnType());
    }

    public void close() throws IOException {

    }

    public static String getCodeBase(Class<?> cls) {
        if (cls == null)
            return null;
        ProtectionDomain domain = cls.getProtectionDomain();
        if (domain == null)
            return null;
        CodeSource source = domain.getCodeSource();
        if (source == null)
            return null;
        URL location = source.getLocation();
        if (location == null)
            return null;
        return location.getFile();
    }
}
