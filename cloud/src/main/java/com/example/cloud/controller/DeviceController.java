package com.example.cloud.controller;

import api.DeviceService;
import api.MessageService;
import com.example.cloud.config.NettyConfig;
import model.DeviceModel;
import model.MsgModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/cloudDevice/")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;
    @GetMapping("getDevice")
    public List<DeviceModel> getDevice(){
        return null;
    }
    @PostMapping("operateDevice")
    public void operateDevice(@RequestBody DeviceModel deviceModel) {
        deviceService.operateDevice(deviceModel);
    }
}
