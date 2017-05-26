package com.zrk1000.demo.repository;

import com.zrk1000.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/2
 * Time: 15:11
 */
//@Repository
public interface UserRepository  extends JpaRepository<User,Long> {

    /**
     * 根据用户名查询  获取一条数据
     * @param name
     * @return
     */
    User findTopByName(String name);

    /**
     * 根据用户名获取所有数据
     * @param name
     * @return
     */
    List<User> findByName(String name);

    /**
     * 根据分组查询所有用户
     * @param groupId
     * @return
     */
    List<User> findByGroupId(Long groupId);


    /**
     * 查询所有用户数
     * @return
     */
    Integer countBy();

}

