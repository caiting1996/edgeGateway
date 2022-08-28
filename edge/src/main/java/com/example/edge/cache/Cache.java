package com.example.edge.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
@Component
public class Cache {
    private ConcurrentHashMap map=new ConcurrentHashMap();
    public void put(String key,Object value){
        map.put(key,value);

    }
    public Object get(String key){
        return map.get(key);
    }
    public ConcurrentHashMap getMap(){
        return this.map;
    }
    public void delete(String key){
        map.remove(key);
    }

}
