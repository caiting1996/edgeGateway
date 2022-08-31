package com.example.edge.db;

import api.DataStorage;
import model.DeviceModel;
import org.springframework.stereotype.Component;
import util.FileUtil;
import util.JsonUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component("fileStorage")
public class FileStorage implements DataStorage {
    private static final String FILE_POST=".txt";
    @Override
    public void test(){
        System.out.println("fileStorage");
    }

    @Override
    public void storageData(DeviceModel deviceModel) throws IOException {
        FileUtil.writeToFile(deviceModel.getDeviceId()+FILE_POST, JsonUtil.obj2String(deviceModel));
    }

    @Override
    public DeviceModel getData(String deviceId) {
        return FileUtil.readFile(deviceId+FILE_POST);
    }

    @Override
    public void updateData(DeviceModel deviceModel) {
        DeviceModel device=FileUtil.readFile(deviceModel.getDeviceId()+FILE_POST);
        Set set=deviceModel.getDeviceInfo().entrySet();
        Iterator<Map.Entry<String, Map>> iterator = set.iterator();
        while (iterator.hasNext()){
            String key=iterator.next().getKey();
            Object value=iterator.next().getValue();
            device.getDeviceInfo().put(key,value);
        }


    }
}
