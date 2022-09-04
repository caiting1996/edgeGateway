package com.example.cloud.util;

import model.DeviceModel;
import model.MsgModel;
import util.JsonUtil;

import java.io.*;

public class FileUtil {

    public void writeToFile(String fileName, String s) throws IOException {
        File file = new File(fileName);
        OutputStream out = null;
        BufferedWriter bw = null;
        file.createNewFile();
        out = new FileOutputStream(file);
        bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
        bw.write(s);
        bw.flush();
        bw.close();

    }
    public String readFile(String fileName){
        String s=null;
        try{
            InputStream is = new FileInputStream(fileName);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            s=new String(bytes);
            System.out.println(s);
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return s;
    }

    public void deleteFile(String fileName){
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

}
