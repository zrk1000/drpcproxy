package com.zrk1000.demo.controller;

import com.zrk1000.demo.dto.GroupDto;
import com.zrk1000.demo.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity getGroup(@PathVariable("id") Long id){
        return new ResponseEntity(groupService.getGroup(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public ResponseEntity getGroups(){
        return new ResponseEntity(groupService.getGroups(), HttpStatus.OK);
    }


    @RequestMapping(value = "/init",method = RequestMethod.GET)
    public ResponseEntity getUser(){
        List<GroupDto> groups = groupService.init();
        return new ResponseEntity(groups, HttpStatus.OK);
    }

}

    