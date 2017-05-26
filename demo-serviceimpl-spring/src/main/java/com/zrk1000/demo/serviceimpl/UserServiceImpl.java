package com.zrk1000.demo.serviceimpl;


import com.zrk1000.demo.dto.UserDto;
import com.zrk1000.demo.entity.User;
import com.zrk1000.demo.repository.UserRepository;
import com.zrk1000.demo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rongkang on 2017-03-11.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDto getUser(Long id) {
        return convert(userRepository.findOne(id));
    }

    public UserDto getUserByName(String name) {
        return convert(userRepository.findTopByName(name));
    }

    public List<UserDto> getUsers(String name) {
        if(name==null)
            return converts(userRepository.findAll());
        return converts(userRepository.findByName(name));

    }

    public List<UserDto> getUsersByGroup(Long groupId) {
        return converts(userRepository.findByGroupId(groupId));
    }

    public Integer getCount() {
        return userRepository.countBy();
    }




    //转换为dto
    private UserDto convert(User user){
        UserDto dto = null;
        if(user!=null){
            dto = new UserDto();
            BeanUtils.copyProperties(user,dto);
        }
        return dto;
    }
    //转换为List<dto>
    private List<UserDto> converts(List<User> users){
        List<UserDto> userDtos = new ArrayList<UserDto>();
        if(users!=null)
            for (User user : users)
                userDtos.add(convert(user));
        return userDtos;
    }


}
