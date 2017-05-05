package com.zrk1000.proxy.utils;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/5/5
 * Time: 14:51
 */
public class ReflectUtil {

    public static String getCodeBase(Class<?> cls) {
        if (cls == null)
            return null;
        ProtectionDomain domain = cls.getProtectionDomain();
        if (domain == null)
            return null;
        CodeSource source = domain.getCodeSource();
        if (source == null)
            return null;
        URL location = source.getLocation();
        if (location == null)
            return null;
        return location.getFile();
    }
}

    