package com.zrk1000.demo.serviceimpl;

import com.zrk1000.demo.dto.GroupDto;
import com.zrk1000.demo.dto.UserDto;
import com.zrk1000.demo.entity.Group;
import com.zrk1000.demo.entity.User;
import com.zrk1000.demo.repository.GroupRepository;
import com.zrk1000.demo.repository.UserRepository;
import com.zrk1000.demo.service.GroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


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

    @Autowired
    private UserRepository userRepository;

    public GroupDto getGroup(Long id) {
        if(id<0)
            throw new RuntimeException("参数错误");
        return convert(groupRepository.findOne(id));
    }

    public List<GroupDto> init() {

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
        user3.setGroup(group);


        users.add(user1);
        users.add(user2);
        users.add(user3);
        userRepository.save(users);

        List<Group> groups = groupRepository.findAll();
        return converts(groups);

    }

    public List<GroupDto> getGroups() {
        List<Group> groups = groupRepository.findAll();
        return converts(groups);
    }

    //转换为dto
    private GroupDto convert(Group group){
        GroupDto dto = null;
        if(group!=null){
            dto = new GroupDto();
            BeanUtils.copyProperties(group,dto);
            if(group.getUsers()!=null){
                List<UserDto> userDtos = new ArrayList<UserDto>();
                for (User user : group.getUsers()){
                    UserDto userDto = new UserDto();
                    BeanUtils.copyProperties(user,userDto);
                    userDtos.add(userDto);
                }
                dto.setUser(userDtos);
            }
        }
        return dto;
    }
    private List<GroupDto> converts(List<Group> groups){
        List<GroupDto> groupDtos = new ArrayList<GroupDto>();
        if(groups!=null)
            for (Group group : groups)
                groupDtos.add(convert(group));
        return groupDtos;
    }

}

    