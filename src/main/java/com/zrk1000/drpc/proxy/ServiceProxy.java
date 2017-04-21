package com.zrk1000.drpc.proxy;

import com.zrk1000.drpc.rpc.RpcHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rongkang on 2017-03-11.
 */
public  class ServiceProxy<T> implements InvocationHandler,Serializable {

    private static Logger logger = LoggerFactory.getLogger(ServiceProxy.class);

    private final RpcHandle rpcHandle;

    private final Class<T> serviceInterface;

    private final  Map<Method, ServiceMethod> methodCache = new ConcurrentHashMap<Method, ServiceMethod>();

    public ServiceProxy(RpcHandle rpcHandle, Class<T> serviceInterface) {
        this.rpcHandle = rpcHandle;
        this.serviceInterface = serviceInterface;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //Object 中的方法
        if(Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Throwable t) {
                throw new Exception("Object method invoke exception!");
            }
        //业务方法
        } else {
            ServiceMethod serviceMethod = cachedServiceMethod(method);
            if(logger.isDebugEnabled())
                logger.debug("execution proxy method ,clazz : {} , method : {} , parameters : {} ， returnType : {}",
                        serviceMethod.getClazz(),serviceMethod.getMethodName(),serviceMethod.getParams().values(),serviceMethod.getReturnType());
            return rpcHandle.exec(serviceMethod,args);
        }
    }

    private ServiceMethod cachedServiceMethod(Method method) {
        ServiceMethod serviceMethod = this.methodCache.get(method);
        if(serviceMethod == null) {
            serviceMethod = new ServiceMethod(this.serviceInterface, method);
            this.methodCache.put(method, serviceMethod);
        }
        return serviceMethod;
    }
}
