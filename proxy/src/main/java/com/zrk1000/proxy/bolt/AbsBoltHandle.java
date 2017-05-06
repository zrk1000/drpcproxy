package com.zrk1000.proxy.bolt;

import com.alibaba.fastjson.JSON;
import com.zrk1000.proxy.proxy.ServiceMethod;
import com.zrk1000.proxy.rpc.drpc.DrpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rongkang on 2017-05-06.
 */
public abstract class AbsBoltHandle implements BoltHandle {

    private Map<String,Map<Integer ,Method>> methodCache = new ConcurrentHashMap();

    public Object invoke(Object impl,Method method,Object[] params) throws InvocationTargetException, IllegalAccessException {
        Object[] params_final = null;
        if(params!=null){
            params_final = new Object[params.length];
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++)
                params_final[i] = JSON.parseObject(JSON.toJSONString(params[i]),parameterTypes[i]);
        }
        return method.invoke(impl,params_final);
    }

    public Method getMethod( DrpcRequest request) throws ClassNotFoundException {
        Map<Integer, Method> methodMap = methodCache.get(request.getInterfaceClazz());
        if(methodMap == null){
            Class<?> interfaceClass = Class.forName(request.getInterfaceClazz());
            methodMap = new ConcurrentHashMap();
            Method[] methods = interfaceClass.getMethods();
            for (Method method:methods)
                methodMap.put(new ServiceMethod(interfaceClass, method).hashCode(),method);
            methodCache.put(request.getInterfaceClazz(),methodMap);
        }
        return methodMap.get(request.getMethodHashCode());
    }
}
