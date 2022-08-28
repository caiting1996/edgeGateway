package com.example.cloud.controller;

import com.example.cloud.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/v1/cloud/")
public class Controller {
    @Autowired
    private MessageService messageService;

    @PostMapping("sendMsg")
    public String sendMsg(@RequestBody Map<String, String> map) throws ExecutionException, InterruptedException {
        messageService.sendMsg(map.get("msg"), map.get("uid"));
        return "success";
    }
}

