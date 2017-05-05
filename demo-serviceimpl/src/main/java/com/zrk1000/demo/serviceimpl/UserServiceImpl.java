package com.zrk1000.demo.serviceimpl;

import com.zrk1000.demo.exception.BizException;
import com.zrk1000.demo.exception.MyException;
import com.zrk1000.demo.exception.MyOnlyException;
import com.zrk1000.demo.model.User;
import com.zrk1000.demo.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rongkang on 2017-03-11.
 */

public class UserServiceImpl implements UserService {

    public User getUser(String name) throws MyException{
        User user = new User();
        if("tom".equals(name)){
            user.setAge(12);
            user.setId(111L);
            user.setName("tom");

        }else {
            throw new MyOnlyException("业务异常");
        }
        return user;
    }

//    public static void main(String[] args) {
//        UserService userService = new UserServiceImpl();
//        Method[] methods = UserService.class.getMethods();
//        try {
//            methods[0].invoke(userService,"tom1");
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            System.out.println(e.getCause());
//            System.out.println(e.getTargetException().getClass().getName());
//            try {
//                boolean equals = Class.forName(e.getTargetException().getClass().getName()).equals(ArithmeticException.class);
//                System.out.println(equals);
//            } catch (ClassNotFoundException e1) {
//                e1.printStackTrace();
//            }
//        }

//        RuntimeException runtimeException = new RuntimeException("业务异常");
//
//        String s = JSON.toJSONString(runtimeException);
//
//        RuntimeException runtimeException1 = JSON.parseObject(s, RuntimeException.class);
//
//        System.out.println(runtimeException.equals(runtimeException1));

//        BizException bizException = new BizException(10040007, "序列生成超时");
//        String s = JSON.toJSONString(bizException);
//        System.out.println(s);
//        BizException runtimeException1 = JSON.parseObject(s, BizException.class);
//        System.out.println(bizException.equals(runtimeException1));


//    }


}
