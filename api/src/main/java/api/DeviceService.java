package api;

import model.DeviceModel;

public interface DeviceService {
    void registerDevice(DeviceModel deviceModel);
    void deleteDevice(DeviceModel deviceModel);
    void operateDevice(DeviceModel deviceModel);

}
