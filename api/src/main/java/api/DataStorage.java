package api;

import model.DeviceModel;
import model.MsgModel;

import java.io.IOException;
import java.util.Map;

public interface DataStorage {
    void test();
    void storageData(DeviceModel deviceModel) throws IOException;
    DeviceModel getData(String deviceId);
    void updateData(DeviceModel deviceModel);
    void deleteData(String deviceId);
    void storageData(Map map) throws IOException;
}
