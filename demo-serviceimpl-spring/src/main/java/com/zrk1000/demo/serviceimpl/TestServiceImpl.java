package com.zrk1000.demo.serviceimpl;

import com.zrk1000.demo.service.TestService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/4/27
 * Time: 17:11
 */
@Service
public class TestServiceImpl implements TestService{

    public void retunrVoid() {
        System.out.println("===========do retunrVoid=========");
    }

    public void basedTypeParameter(int a, long b, double c, boolean d, byte e, char f, float g, short h) {
        System.out.println("===========do basedTypeParameter=========");
        System.out.println(String.format("parameters:a:{},b:{},c:{},d:{},e:{},f:{},g:{},h:{}",a,b,c,d,e,f,g,h));
    }

    public Map<String, String> complexTypes(List<String> list) {
        System.out.println("===========do complexTypes=========");
        System.out.println(String.format("parameters:",list));
        Map<String, String> map = new HashMap<String, String>();
        map.put("name","zrk1000--");
        map.put("age","26--");
        return map;
    }
}

    