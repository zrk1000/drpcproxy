package com.zrk1000.demo.service;

import com.zrk1000.demo.dto.UserDto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/2
 * Time: 16:55
 */
public interface UserService {

    UserDto getUser(Long id);

    UserDto getUserByName(String name);

    List<UserDto> getUsers(String name);

    List<UserDto> getUsersByGroup(Long groupId);

    Integer getCount();



}

    