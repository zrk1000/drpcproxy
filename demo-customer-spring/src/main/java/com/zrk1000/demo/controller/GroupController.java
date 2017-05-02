package com.zrk1000.demo.controller;

import com.zrk1000.demo.dto.GroupDto;
import com.zrk1000.demo.dto.UserDto;
import com.zrk1000.demo.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/2
 * Time: 16:51
 */
@RestController
@RequestMapping("group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public GroupDto getUser(@PathVariable("id") Long id){
        return groupService.getGroup(id);
    }
}

    