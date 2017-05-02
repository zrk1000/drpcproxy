package com.zrk1000.demo.dto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/2
 * Time: 16:55
 */
public class GroupDto {

    private Long id;

    private String name;

    private List<UserDto> user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserDto> getUser() {
        return user;
    }

    public void setUser(List<UserDto> user) {
        this.user = user;
    }
}

    