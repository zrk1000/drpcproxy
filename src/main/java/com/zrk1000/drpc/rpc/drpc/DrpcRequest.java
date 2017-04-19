package com.zrk1000.drpc.rpc.drpc;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by rongkang on 2017-04-03.
 */
public class DrpcRequest implements Serializable{

    private String interfaceClazz;

    private String method;

    private Integer methodHashCode;

    private Object[] params;

    public DrpcRequest() {
    }

    public DrpcRequest(String interfaceClazz, String method,Integer methodHashCode, Object[] params) {
        this.interfaceClazz = interfaceClazz;
        this.method = method;
        this.methodHashCode = methodHashCode;
        this.params = params;
    }

    public DrpcRequest parseJSONString(String jsonStr) {
        return JSONObject.parseObject(jsonStr, this.getClass());
    }

    public String toJSONString(){
        return JSONObject.toJSONString(this);
    }

    public String getInterfaceClazz() {
        return interfaceClazz;
    }

    public void setInterfaceClazz(String interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Integer getMethodHashCode() {
        return methodHashCode;
    }

    public void setMethodHashCode(Integer methodHashCode) {
        this.methodHashCode = methodHashCode;
    }
}
