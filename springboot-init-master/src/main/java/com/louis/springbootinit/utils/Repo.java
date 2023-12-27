package com.louis.springbootinit.utils;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/25 22:22
 */
public class Repo {
    public static Object repo;
    public static void save(Object t){
        repo = t;
    }
    public static Object get(){
        return repo;
    }
    public static void remove(){
        repo = null;
    }
}
