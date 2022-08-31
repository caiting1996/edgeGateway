package com.example.edge.db;

import api.DataStorage;
import model.DeviceModel;
import org.springframework.stereotype.Component;

@Component("dataBaseStorage")
public class DataBaseStorage implements DataStorage {
    @Override
    public void test(){
        System.out.println("dataBaseStorage");
    }

    @Override
    public void storageData(DeviceModel deviceModel) {

    }

    @Override
    public DeviceModel getData(String deviceId) {
        return null;
    }

    @Override
    public void updateData(DeviceModel deviceModel) {

    }
}
