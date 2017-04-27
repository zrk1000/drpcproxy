package com.zrk1000.demo.service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/4/27
 * Time: 17:06
 */
public interface TestService {

    void retunrVoid();

    void basedTypeParameter(int a,long b,double c,boolean d,byte e,char f ,float g,short h);

    Map<String,String>   complexTypes(List<String> list);


}
