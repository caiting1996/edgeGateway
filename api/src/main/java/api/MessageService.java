package api;

import model.MsgModel;

public interface MessageService {
    void sendMsg(MsgModel msgModel);
    void receiveMsg(MsgModel msgModel);
    void storageMsg(MsgModel msgModel);
    void handleMsg(MsgModel msgModel);
}
