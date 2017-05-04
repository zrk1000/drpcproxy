package com.zrk1000.demo.serviceimpl;

import com.zrk1000.demo.model.User;
import com.zrk1000.demo.service.UserService;
import sun.dc.path.PathException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by rongkang on 2017-03-11.
 */

public class UserServiceImpl implements UserService {

    public User getUser(String name) throws PathException {
        User user = new User();
        if("tom".equals(name)){
            user.setAge(12);
            user.setId(111L);
            user.setName("tom");

        }else {
            int a = 1/0;
//            throw new RuntimeException("业务异常");
        }
        return user;
    }

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        Method[] methods = UserService.class.getMethods();
        try {
            methods[0].invoke(userService,"tom1");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.out.println(e.getCause());
            System.out.println(e.getTargetException().getClass().getName());
            try {
                boolean equals = Class.forName(e.getTargetException().getClass().getName()).equals(ArithmeticException.class);
                System.out.println(equals);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }


}
