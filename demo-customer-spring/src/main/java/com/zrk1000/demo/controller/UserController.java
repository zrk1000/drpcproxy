package com.zrk1000.demo.controller;

import com.zrk1000.demo.dto.UserDto;
import com.zrk1000.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/2
 * Time: 16:51
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public UserDto getUser(@PathVariable("id") Long id){
        return userService.getUser(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public UserDto getUser(@RequestParam(required = true) String name){
        return userService.getUserByName(name);
    }

    @RequestMapping(value="/list",method = RequestMethod.GET)
    public List<UserDto> getUsers(@RequestParam(required = false) String name){
        return userService.getUsers(name);
    }

    @RequestMapping(value="/group",method = RequestMethod.GET)
    public List<UserDto> getUsers(@RequestParam(required = false) Long groupId){
        return userService.getUsersByGroup(groupId);
    }

    @RequestMapping(value="/count",method = RequestMethod.GET)
    public Integer count(){
        return userService.getCount();
    }

}

    