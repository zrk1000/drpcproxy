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
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/2
 * Time: 18:02
 */
//@ContextConfiguration(classes = ApplicationConfig.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class GroupTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    JpaProperties  jpaProperties;

    @Test
    public void insert(){

        List<User> users = new ArrayList<User>();

        Group group = new Group();
        group.setName("管理员");
        group.setUsers(users);
        groupRepository.save(group);

        User user1 = new User();
        user1.setName("周荣康");
        user1.setAge(26);
        user1.setGroup(group);

        User user2 = new User();
        user2.setName("周康");
        user2.setAge(25);
        user2.setGroup(group);


        User user3 = new User();
        user3.setName("张广斐");
        user3.setAge(22);
        user2.setGroup(group);


        users.add(user1);
        users.add(user2);
        users.add(user3);
        users = userRepository.save(users);



        List<User> all = userRepository.findAll();
        for (User user:all)
            System.out.println(user);
        Assert.assertNotNull(users);
        Assert.assertNotNull(users.size());
        Assert.assertNotNull(users.get(0).getId());
        Assert.assertNotNull(users.get(1).getId());
        Assert.assertNotNull(users.get(2).getId());
    }


}

    