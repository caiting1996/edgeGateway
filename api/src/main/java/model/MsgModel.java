package model;

import lombok.Data;

@Data
public class MsgModel {
    private String type;
    private DeviceModel deviceModel;
    private String userId;
}
