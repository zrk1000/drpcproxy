package com.zrk1000.proxy.bolt;

import com.zrk1000.proxy.rpc.drpc.DrpcRequest;
import com.zrk1000.proxy.rpc.drpc.DrpcResponse;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/4/28
 * Time: 11:07
 */
public interface BoltHandle {

    DrpcResponse _execute(DrpcRequest drpcRequest);

}
