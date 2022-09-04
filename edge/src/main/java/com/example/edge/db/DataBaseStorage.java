package com.example.edge.db;

import api.DataStorage;
import model.DeviceModel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

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
    public String getData(String deviceId) {
        return null;
    }

    @Override
    public void updateData(DeviceModel deviceModel) {

    }

    @Override
    public void deleteData(String deviceId) {

    }

    @Override
    public void storageData(Map map) throws IOException {

    }
}
