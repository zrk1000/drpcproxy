package com.zrk1000.demo.service;


import com.zrk1000.demo.model.User;
import sun.dc.path.PathException;

/**
 * Created by rongkang on 2017-03-11.
 */
public interface UserService {

    User getUser(String name) throws PathException;


}
