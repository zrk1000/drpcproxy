package com.zrk1000.demo.serviceimpl;

import com.zrk1000.demo.dto.GroupDto;
import com.zrk1000.demo.entity.Group;
import com.zrk1000.demo.repository.GroupRepository;
import com.zrk1000.demo.service.GroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/2
 * Time: 17:07
 */

@Service
@Transactional
public class GroupServiceImpl implements GroupService{

    @Autowired
    private GroupRepository groupRepository;

    public GroupDto getGroup(Long id) {
        return convert(groupRepository.findOne(id));
    }

    private GroupDto convert(Group group){
        //转换为dto
        GroupDto dto = new GroupDto();
        BeanUtils.copyProperties(group,dto);
        return dto;
    }
}

    