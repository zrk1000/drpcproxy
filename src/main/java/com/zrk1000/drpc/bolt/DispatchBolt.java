package com.zrk1000.drpc.bolt;

import com.alibaba.fastjson.JSON;
import com.zrk1000.drpc.proxy.ServiceMethod;
import com.zrk1000.drpc.rpc.drpc.DrpcRequest;
import com.zrk1000.drpc.rpc.drpc.DrpcResponse;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rongkang on 2017-04-03.
 */
public class DispatchBolt extends BaseBasicBolt {

    private ConfigurableApplicationContext applicationContext;

    private String[] basePackages;

    private Map<String,Map<Integer ,Method>> beanCache= new ConcurrentHashMap();

    public DispatchBolt(String[] basePackages) {
        this.basePackages = basePackages;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(basePackages);
        applicationContext.start();
        this.applicationContext = applicationContext;
    }



    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String param = tuple.getString(0);
        DrpcRequest request = JSON.parseObject(param,DrpcRequest.class);
        DrpcResponse response = new DrpcResponse();
        response.setCode(200);
        response.setMsg("success");
        Object result = null;
        try {
            Class<?> interfaceClass = Class.forName(request.getInterfaceClazz());
            Object bean = applicationContext.getBean(interfaceClass);
            Method method = getMethod(request,interfaceClass);
            result = invoke(method, bean, request.getParams());
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
        }

        collector.emit(new Values(JSON.toJSONString(response), tuple.getValue(1)));
    }


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("result", "return-info"));
    }

    @Override
    public void cleanup() {
    }

    private Object invoke(Method method,Object bean,Object[] params) throws InvocationTargetException, IllegalAccessException {
        Object[] params_final = new Object[params.length];
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            params_final[i] = JSON.parseObject(JSON.toJSONString(params[i]),parameterTypes[i]);
        return method.invoke(bean,params_final);
    }

    private Method getMethod( DrpcRequest request,Class<?> interfaceClass){
        Map<Integer, Method> methodMap = beanCache.get(request.getInterfaceClazz());
        if(methodMap == null){
            methodMap = new ConcurrentHashMap();
            Method[] methods = interfaceClass.getMethods();
            for (Method method:methods)
                methodMap.put(new ServiceMethod(interfaceClass, method).hashCode(),method);
            beanCache.put(request.getInterfaceClazz(),methodMap);
        }else {
            methodMap =  beanCache.get(request.getInterfaceClazz());
        }
        return methodMap.get(request.getMethodHashCode());
    }

}
