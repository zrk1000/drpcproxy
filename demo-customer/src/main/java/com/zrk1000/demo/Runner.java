package com.zrk1000.demo;

import com.zrk1000.demo.exception.MyException;
import com.zrk1000.demo.model.User;
import com.zrk1000.demo.service.TestService;
import com.zrk1000.demo.service.UserService;
import com.zrk1000.proxy.ServiceImplFactory;


import java.util.ArrayList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/4/27
 * Time: 9:44
 */
public class Runner {

    public static void main(String[] args) {

        ServiceImplFactory.init();
        UserService userService = ServiceImplFactory.newInstance(UserService.class);
        TestService testService = ServiceImplFactory.newInstance(TestService.class);

        User user = null;
        try {
            user = userService.getUser("tom1");
            System.out.println("------------user:"+user.toString());
            testService.retunrVoid();
            testService.basedTypeParameter(1,2L,3D,true,(byte)5,'6',7.0F,(short)8);

            ArrayList<String> list = new ArrayList<String>();
            list.add("aaaaa");
            list.add("bbbbb");
            Map<String, String> map = testService.complexTypes(list);

            System.out.println(map.toString());
        } catch (MyException e) {
            e.printStackTrace();
        }

//        synchronized (Runner.class) {
//            while (true) {
//                try {
//                    Runner.class.wait();
//                } catch (InterruptedException e) {
//                    System.err.println("== synchronized error:"+e);
//                }
//            }
//        }
    }
}

    