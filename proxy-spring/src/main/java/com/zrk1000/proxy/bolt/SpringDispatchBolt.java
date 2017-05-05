package com.zrk1000.proxy.bolt;

import com.zrk1000.proxy.rpc.drpc.DrpcRequest;
import com.zrk1000.proxy.rpc.drpc.DrpcResponse;
import org.apache.storm.task.TopologyContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
public class SpringDispatchBolt extends AbsDispatchBolt{

    private Map<String,Class<?>> interfaceClassCache = new ConcurrentHashMap();

    private ConfigurableApplicationContext applicationContext;

    private String[] basePackages;

    public void init(Object object) {
        String[] basePackages = (String[]) object;
        this.basePackages = basePackages;
    }

    public void _prepare(Map stormConf, TopologyContext context) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(basePackages);
        applicationContext.start();
        this.applicationContext = applicationContext;
    }



    public DrpcResponse _execute(DrpcRequest request) {
        DrpcResponse response = new DrpcResponse();
        response.setCode(200);
        response.setMsg("success");
        Object result = null;
        try {
            Object bean = applicationContext.getBean(getInterfaceClass(request.getInterfaceClazz()));
            Method method = getMethod(request);
            result = invoke(bean, method, request.getParams());
            response.setData(result);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setException(e.getClass().getName(),e.getMessage(),e.getStackTrace());
            response.setMsg(e.getMessage());
            response.setCode(404);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            response.setException(e.getClass().getName(),e.getMessage(),e.getStackTrace());
            response.setMsg(e.getMessage());
            response.setCode(400);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            response.setMsg(e.getMessage());
            response.setException(e.getTargetException().getClass().getName(),e.getTargetException().getMessage(),e.getTargetException().getStackTrace());
            response.setCode(500);
        }

        return response;

    }

   private Class<?> getInterfaceClass(String interfaceClazz) throws ClassNotFoundException {
       Class<?> aClass = interfaceClassCache.get(interfaceClazz);
       if(aClass==null){
           interfaceClassCache.put(interfaceClazz,Class.forName(interfaceClazz));
           aClass = interfaceClassCache.get(interfaceClazz);
       }
       return aClass;
   }
}

    