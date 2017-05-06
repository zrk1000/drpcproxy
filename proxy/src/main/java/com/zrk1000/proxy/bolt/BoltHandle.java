package com.zrk1000.proxy.bolt;

import com.zrk1000.proxy.rpc.drpc.DrpcRequest;
import com.zrk1000.proxy.rpc.drpc.DrpcResponse;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/4/28
 * Time: 11:07
 */
public interface BoltHandle extends Serializable{

    void prepare();

    DrpcResponse execute(DrpcRequest drpcRequest);

}
