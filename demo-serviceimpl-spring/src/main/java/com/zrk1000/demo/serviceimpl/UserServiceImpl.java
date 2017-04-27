package com.zrk1000.demo.serviceimpl;


import com.zrk1000.demo.model.User;
import com.zrk1000.demo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Created by rongkang on 2017-03-11.
 */
@Service
public class UserServiceImpl implements UserService {

    public User getUser(String name){
        User user = new User();
        if("tom".equals(name)){
            user.setAge(12);
            user.setId(111L);
            user.setName("tom");

        }else {
            throw new RuntimeException("业务异常");
        }
        return user;
    }

}
