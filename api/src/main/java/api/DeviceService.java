package api;

import model.DeviceModel;

import java.io.IOException;

public interface DeviceService {
    void registerDevice(DeviceModel deviceModel) throws IOException;
    void deleteDevice(DeviceModel deviceModel);
    void operateDevice(DeviceModel deviceModel);

}
