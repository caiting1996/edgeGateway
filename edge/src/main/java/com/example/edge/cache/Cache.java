package com.example.edge.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 边缘端缓存，用于与云端断连时缓存消息
 */
@Component
public class Cache {
    private static ConcurrentHashMap map=new ConcurrentHashMap();
    public static void put(String key,Object value){
        map.put(key,value);

    }
    public static Object get(String key){
        return map.get(key);
    }
    public static ConcurrentHashMap getMap(){
        return map;
    }
    public static void delete(String key){
        map.remove(key);
    }

}
