package com.zrk1000.demo.service;


import com.zrk1000.demo.exception.MyException;
import com.zrk1000.demo.model.User;

/**
 * Created by rongkang on 2017-03-11.
 */
public interface UserService {

    User getUser(String name) throws MyException;


}
