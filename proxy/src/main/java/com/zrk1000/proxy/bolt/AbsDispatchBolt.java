package com.zrk1000.proxy.bolt;

import com.alibaba.fastjson.JSON;
import com.zrk1000.proxy.proxy.ServiceMethod;
import com.zrk1000.proxy.rpc.drpc.DrpcRequest;
import com.zrk1000.proxy.rpc.drpc.DrpcResponse;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rongkang on 2017-04-03.
 */
public abstract class AbsDispatchBolt extends BaseBasicBolt {

    private Map<String,Map<Integer ,Method>> methodCache = new ConcurrentHashMap();

    public abstract void init(Object object);

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf,context);
        _prepare(stormConf,context);
    }

    public abstract void _prepare(Map stormConf, TopologyContext context);

    public abstract DrpcResponse _execute(DrpcRequest drpcRequest);

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String param = tuple.getString(0);
        DrpcRequest request = JSON.parseObject(param,DrpcRequest.class);
        DrpcResponse response = _execute(request);
        collector.emit(new Values(JSON.toJSONString(response), tuple.getValue(1)));
    }


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("result", "return-info"));
    }

    @Override
    public void cleanup() {
    }

    public Object invoke(Method method,Object impl,Object[] params) throws InvocationTargetException, IllegalAccessException {
        Object[] params_final = new Object[params.length];
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            params_final[i] = JSON.parseObject(JSON.toJSONString(params[i]),parameterTypes[i]);
        return method.invoke(impl,params_final);
    }

    public Method getMethod( DrpcRequest request,Class<?> interfaceClass){
        Map<Integer, Method> methodMap = methodCache.get(request.getInterfaceClazz());
        if(methodMap == null){
            methodMap = new ConcurrentHashMap();
            Method[] methods = interfaceClass.getMethods();
            for (Method method:methods)
                methodMap.put(new ServiceMethod(interfaceClass, method).hashCode(),method);
            methodCache.put(request.getInterfaceClazz(),methodMap);
        }else {
            methodMap =  methodCache.get(request.getInterfaceClazz());
        }
        return methodMap.get(request.getMethodHashCode());
    }

}
