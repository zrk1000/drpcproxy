package com.zrk1000.proxy.rpc.drpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zrk1000.proxy.proxy.ServiceMethod;
import com.zrk1000.proxy.rpc.RpcHandle;
import com.zrk1000.proxy.utils.ReflectUtil;
import com.zrk1000.proxy.utils.SerializableUtil;
import org.apache.storm.generated.DistributedRPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by rongkang on 2017-05-06.
 */
public abstract class AbsDrpcHandler implements RpcHandle {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public abstract DistributedRPC.Iface getDrpcClient() throws Throwable;

    public void postProcess(DistributedRPC.Iface drpcClient){}

    public abstract String getDrpcService(ServiceMethod serviceMethod);

    @Override
    public Object exec(ServiceMethod serviceMethod, Object[] args) throws Throwable {
        DrpcResponse drpcResponse = new DrpcResponse();
        String drpcService = getDrpcService(serviceMethod);
        DistributedRPC.Iface drpcClient = null;
        try{
            drpcClient = getDrpcClient();
            String result = drpcClient.execute(drpcService, new DrpcRequest(serviceMethod.getClazz(),serviceMethod.getMethodName(),serviceMethod.hashCode(),args).toJSONString());
            if(logger.isDebugEnabled())
                logger.debug("The remote invocation returns the result:"+ result);
            if(result!=null)
                drpcResponse = JSON.parseObject(result, DrpcResponse.class);

            if (drpcResponse.hasException()) {
                Throwable exception = SerializableUtil.StrToObj(drpcResponse.getException(),Throwable.class);
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
                String serviceFile = ReflectUtil.getCodeBase(serviceMethod.getServiceInterface());
                String exceptionFile = ReflectUtil.getCodeBase(exception.getClass());
                if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)){
                    throw exception;
                }
                // 是JDK自带的异常，直接抛出
                String className = exception.getClass().getName();
                if (className.startsWith("java.") || className.startsWith("javax.")) {
                    throw exception;
                }

                // 否则，包装成RuntimeException抛给客户端
                RuntimeException runtimeException = new RuntimeException(exception.getClass().getName() + " : " + exception.getMessage());
                runtimeException.setStackTrace(exception.getStackTrace());
                throw runtimeException;

            }
        }finally {
            postProcess(drpcClient);
        }
        return JSONObject.parseObject(JSON.toJSONString(drpcResponse.getData()), serviceMethod.getReturnType());
    }

}
