package com.zrk1000.proxy.bolt;

import com.zrk1000.proxy.rpc.drpc.DrpcRequest;
import com.zrk1000.proxy.rpc.drpc.DrpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rongkang on 2017-05-06.
 */
public class ConfigBoltHandle  extends AbsBoltHandle{

    private Map<String,Object> serviceImplsMap ;

    private Collection<String> serviceImpls;

    public ConfigBoltHandle(Collection<String> serviceImpls) {
        if(serviceImpls==null || serviceImpls.size() == 0)
            throw new RuntimeException(" Parameter 'service.impls' cannot be empty");
        this.serviceImpls = serviceImpls;
        this.serviceImplsMap = new ConcurrentHashMap();
    }

    public void prepare( ) {
        for (String serviceImpl : serviceImpls){
            try {
                Class<?> serviceImplClass = Class.forName(serviceImpl);
                for (Class<?> _interface:serviceImplClass.getInterfaces() )
                    serviceImplsMap.put(_interface.getCanonicalName(),serviceImplClass.newInstance());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to find the class \""+serviceImpl+"\"");
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to init the class \""+serviceImpl+"\"");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to init the class \""+serviceImpl+"\"");
            }

        }
    }

    public DrpcResponse execute(DrpcRequest request) {
        DrpcResponse response = new DrpcResponse();
        response.setCode(200);
        response.setMsg("success");
        try {
            Object serviceImpl = serviceImplsMap.get(request.getInterfaceClazz());
            if(serviceImpl == null)
                throw  new ClassNotFoundException("Unable to find the class" + request.getInterfaceClazz() );
            Method method = getMethod(request);
            Object result = invoke(serviceImpl, method, request.getParams());
            response.setData(result);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setException(e);
            response.setMsg(e.getMessage());
            response.setCode(404);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            response.setException(e);
            response.setMsg(e.getMessage());
            response.setCode(400);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            response.setMsg(e.getMessage());
            response.setException(e.getTargetException());
            response.setCode(500);
        }
        return response;
    }
}
