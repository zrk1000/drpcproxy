package com.zrk1000.demo.service;

import com.zrk1000.demo.dto.GroupDto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/2
 * Time: 16:55
 */
public interface GroupService {

    GroupDto getGroup(Long id);

    List<GroupDto> init();

    List<GroupDto> getGroups();


}
