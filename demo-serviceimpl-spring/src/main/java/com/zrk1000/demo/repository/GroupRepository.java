package com.zrk1000.demo.repository;

import com.zrk1000.demo.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/2
 * Time: 15:11
 */
@Repository
public interface GroupRepository extends JpaRepository<Group,Long> {

}

