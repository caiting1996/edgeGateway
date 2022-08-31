package com.example.edge.db;

import api.DataStorage;
import com.example.edge.util.SpringExt;


public class DataStorageContext {

    public static DataStorage getFileStorage(){
        return (DataStorage) SpringExt.getApplicationContext().getBean("fileStorage");
    }
    public static DataStorage getDataBaseStorage(){
        return (DataStorage) SpringExt.getApplicationContext().getBean("dataBaseStorage");
    }


}
