package api;

import model.MsgModel;

import java.io.IOException;

public interface MessageService {
    void sendMsg(Object obj);
    void receiveMsg(MsgModel msgModel) throws IOException;
    void storageMsg(MsgModel msgModel) throws IOException;
    void handleMsg(MsgModel msgModel);


}
