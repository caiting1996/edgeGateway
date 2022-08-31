package model;

import lombok.Data;

@Data
public class MsgModel {
    String type;
    private DeviceModel deviceModel;
}
