package com.zrk1000.demo;

import com.zrk1000.demo.entity.Group;
import com.zrk1000.demo.entity.User;
import com.zrk1000.demo.repository.GroupRepository;
import com.zrk1000.demo.repository.UserRepository;
import com.zrk1000.demo.service.GroupService;
import com.zrk1000.demo.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/2
 * Time: 18:02
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class GroupTests {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void insert(){
        List<User> users = new ArrayList<User>();

        User user1 = new User();
        user1.setName("周荣康");
        user1.setAge(26);

        User user2 = new User();
        user2.setName("周康");
        user2.setAge(25);

        User user3 = new User();
        user3.setName("张广斐");
        user3.setAge(22);

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users = userRepository.save(users);

        Assert.assertNotNull(users);
        Assert.assertNotNull(users.size());
        Assert.assertNotNull(users.get(0).getId());
        Assert.assertNotNull(users.get(1).getId());
        Assert.assertNotNull(users.get(2).getId());
    }


}

    