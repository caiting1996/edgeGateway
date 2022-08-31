package model;

import lombok.Data;

import java.util.Map;

@Data
public class DeviceModel {
    private String productId;
    private String deviceId;
    private Map deviceInfo;
}
