package com.zrk1000.proxy.bolt;

import com.zrk1000.proxy.rpc.drpc.DrpcRequest;
import com.zrk1000.proxy.rpc.drpc.DrpcResponse;
import org.apache.storm.task.TopologyContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/4/26
 * Time: 12:38
 */
public class ConfigDispatchBolt extends AbsDispatchBolt{

    private Map<String,Class<?>> serviceImplsMap = new ConcurrentHashMap();

    public void init(Object object) {
        String[] serviceImpls = (String[]) object;
        if(serviceImpls==null || serviceImpls.length == 0)
            throw new RuntimeException("\"service.impls\" are not empty");
        for (String serviceImpl : serviceImpls){
            try {
                Class<?> serviceImplClass = Class.forName(serviceImpl);
                for (Class<?> _interface:serviceImplClass.getInterfaces() )
                    serviceImplsMap.put(_interface.getClass().getCanonicalName(),serviceImplClass);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to find the class \""+serviceImpl+"\"");
            }

        }

    }
    public void _prepare(Map stormConf, TopologyContext context) {

    }



    public DrpcResponse _execute(DrpcRequest request) {
        DrpcResponse response = new DrpcResponse();
        response.setCode(200);
        response.setMsg("success");
        Object result = null;
        try {
            Class<?> interfaceClass = Class.forName(request.getInterfaceClazz());
            Class<?> serviceImpl = serviceImplsMap.get(request.getInterfaceClazz());
            if(serviceImpl == null)
                throw  new ClassNotFoundException("Unable to find the class" + request.getInterfaceClazz() );
            Object impl = serviceImpl.newInstance();
            Method method = getMethod(request,interfaceClass);
            result = invoke(method, impl, request.getParams());
            response.setData(result);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setMsg(e.getMessage());
            response.setCode(404);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            response.setMsg(e.getMessage());
            response.setCode(400);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            response.setMsg(e.getMessage());
            response.setCode(500);
        } catch (InstantiationException e) {
            e.printStackTrace();
            response.setMsg(e.getMessage());
            response.setCode(405);
        }
        return response;

    }
}

    