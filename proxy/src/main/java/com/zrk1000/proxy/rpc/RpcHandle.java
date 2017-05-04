package com.zrk1000.proxy.rpc;



import com.zrk1000.proxy.proxy.ServiceMethod;

import java.io.Closeable;

/**
 * Created by zrk-PC on 2017/4/1.
 */
public interface RpcHandle extends Closeable {

    Object exec(ServiceMethod serviceMethod, Object[] args) throws Throwable;

}
