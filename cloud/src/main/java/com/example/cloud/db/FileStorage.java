package com.example.cloud.db;

import api.DataStorage;
import model.DeviceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.cloud.util.FileUtil;
import util.JsonUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component("fileStorage")
public class FileStorage implements DataStorage {
    @Autowired
    private FileUtil fileUtil;
    private static final String PREFIX="db/cloud/";
    private static final String FILE_POST=".txt";
    @Override
    public void test(){
        System.out.println("fileStorage");
    }

    @Override
    public void storageData(DeviceModel deviceModel) throws IOException {
        fileUtil.writeToFile(PREFIX+deviceModel.getDeviceId()+FILE_POST, JsonUtil.obj2String(deviceModel));
    }

    @Override
    public String getData(String deviceId) {
        return fileUtil.readFile(PREFIX+deviceId+FILE_POST);
    }

    @Override
    public void updateData(DeviceModel deviceModel) {
        DeviceModel device=JsonUtil.string2Obj(fileUtil.readFile(PREFIX+deviceModel.getDeviceId()+FILE_POST),DeviceModel.class);
        Set set=deviceModel.getDeviceInfo().entrySet();
        Iterator<Map.Entry<String, Map>> iterator = set.iterator();
        while (iterator.hasNext()){
            String key=iterator.next().getKey();
            Object value=iterator.next().getValue();
            device.getDeviceInfo().put(key,value);
        }


    }

    @Override
    public void deleteData(String deviceId) {
        fileUtil.deleteFile(PREFIX+deviceId+FILE_POST);
    }

    public void storageData(Map map) throws IOException {
        fileUtil.writeToFile(PREFIX+map.get("userId")+FILE_POST, (String) map.get("token"));
    }
}
