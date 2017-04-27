package com.zrk1000.demo;

import com.zrk1000.demo.model.User;
import com.zrk1000.demo.service.UserService;
import com.zrk1000.proxy.bolt.ConfigDispatchBolt;
import com.zrk1000.proxy.proxy.ServiceProxyFactory;
import com.zrk1000.proxy.rpc.RpcHandle;
import com.zrk1000.proxy.rpc.drpc.StormLocalDrpcHandle;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/4/27
 * Time: 9:44
 */
public class Runner {



    public static void main(String[] args) {


        UserService userService = ServiceProxyFactory.newInstance(UserService.class,new StormLocalDrpcHandle(ConfigDispatchBolt.class));

        try {
            User user = userService.getUser("tom");
            System.out.println(user.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (Runner.class) {
            while (true) {
                try {
                    Runner.class.wait();
                } catch (InterruptedException e) {
                    System.err.println("== synchronized error:"+e);
                }
            }
        }
    }
}

    