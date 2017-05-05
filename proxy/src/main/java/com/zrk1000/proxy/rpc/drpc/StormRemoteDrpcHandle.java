package com.zrk1000.proxy.rpc.drpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zrk1000.proxy.proxy.ServiceMethod;
import com.zrk1000.proxy.rpc.RpcHandle;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.storm.Config;
import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.Set;

/**
 * Created by rongkang on 2017-04-02.
 */
public class StormRemoteDrpcHandle implements RpcHandle {

    private static Logger logger = LoggerFactory.getLogger(StormRemoteDrpcHandle.class);

//    private static GenericObjectPool<DRPCClient> drpcClientPool = null;

    private static Map<String,Set<String>> drpcServiceMap  = null ;

    private static String stormHost;
    private static int stormPort;
    private static int stormTimeout;
    private static Config conf;

    public StormRemoteDrpcHandle(Config conf, String stormHost, Integer stormPort, Integer stormTimeout, Map<String,Set<String>> drpcServiceMap) {
//    public StormRemoteDrpcHandle(Config conf, String stormHost, Integer stormPort, Integer stormTimeout, GenericObjectPoolConfig poolConfig, Map<String,Set<String>> drpcServiceMap) {
        this.conf = conf;
        this.stormHost = stormHost;
        this.stormPort = stormPort;
        this.stormTimeout = stormTimeout;
//        DrpcClientFactory factory = new DrpcClientFactory(conf, stormHost, stormPort, stormTimeout);
//        if(drpcClientPool!=null)
//            return;
//        this.drpcClientPool = new GenericObjectPool<DRPCClient>(factory,poolConfig);
        this.drpcServiceMap = drpcServiceMap;
    }

    public Object exec(ServiceMethod serviceMethod, Object[] args) throws Throwable {
        DRPCClient client = null;
        String result = null;
        DrpcResponse drpcResponse = new DrpcResponse();
        try{
//            client = drpcClientPool.borrowObject();
            client = new DRPCClient(conf,stormHost,stormPort,stormTimeout);
            String drpcService = getDrpcService(serviceMethod.getClazz());
            if(drpcService == null)
                throw  new RuntimeException("Did not match to the remote DRPC, please check the configuration");
            result = client.execute(drpcService, new DrpcRequest(serviceMethod.getClazz(),serviceMethod.getMethodName(),serviceMethod.hashCode(),args).toJSONString());
            if(result!=null)
                drpcResponse = JSON.parseObject(result, DrpcResponse.class);

            if (drpcResponse.hasException()) {
                Exception exception = null;
                try{
                    Class<?> aClass = Class.forName(drpcResponse.getException().getName());
                    Constructor<?> constructor = aClass.getConstructor(String.class);
                    exception = (Exception) constructor.newInstance(drpcResponse.getException().getMessage());
                    exception.setStackTrace(drpcResponse.getException().getStackTraceElements());
                }catch (Exception e){
                    // 否则，包装成RuntimeException抛给客户端
                    RuntimeException runtimeException = new RuntimeException(drpcResponse.getException().getMessage());
                    runtimeException.setStackTrace(drpcResponse.getException().getStackTraceElements());
                    throw runtimeException;
                }
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
                RuntimeException runtimeException = new RuntimeException(drpcResponse.getException().getMessage());
                runtimeException.setStackTrace(drpcResponse.getException().getStackTraceElements());
                throw runtimeException;

            }
        }finally {
            if (client!=null)
                client.close();
//            drpcClientPool.returnObject(client);
        }

        return JSONObject.parseObject(JSON.toJSONString(drpcResponse.getData()), serviceMethod.getReturnType());
    }

    public String getDrpcService(String clazz) {
        for(String key:drpcServiceMap.keySet())
            if(drpcServiceMap.get(key).contains(clazz))
                return key;
        return null;
    }

    public void close() throws IOException {
//        drpcClientPool.close();
        logger.info("drpcClientPool  close!");
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

