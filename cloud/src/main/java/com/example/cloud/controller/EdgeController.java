package com.example.cloud.controller;

import api.MessageService;
import com.example.cloud.config.NettyConfig;
import com.example.cloud.db.DataStorageContext;
import model.MsgModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/v1/cloud/")
public class EdgeController {

    @GetMapping("getEdge")
    public List<String> getEdge(){
        List list=new ArrayList();
        Set set= NettyConfig.getUserChannelMap().entrySet();
        Iterator<Map.Entry<String, Map>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Map> entry = iterator.next();
            list.add(entry.getKey());
        }
        return list;
    }
    @PostMapping("addEdgeUser")
    public String addEdgeUser(@RequestBody Map map) throws IOException {
        String userId= (String) map.get("userId");
        String token= (String) map.get("token");
        DataStorageContext.getFileStorage().storageData(map);
        return "success";
    }



}

