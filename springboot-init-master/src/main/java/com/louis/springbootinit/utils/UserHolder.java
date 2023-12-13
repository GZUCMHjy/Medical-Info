package com.louis.springbootinit.utils;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/10 19:52
 */
public class UserHolder {
    private static final ThreadLocal<Object> tl = new ThreadLocal<>();

    public static void saveUser(Object t){
        tl.set(t);
    }

    public static Object getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
