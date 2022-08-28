package util;

import java.io.*;

public class FileUtil {
    private static final String PREFIX="db/";
    public static void writeToFile(String fileName, String s) throws IOException {
        File file = new File(PREFIX+fileName);
        OutputStream out = null;
        BufferedWriter bw = null;
        if (file.exists()) {
            file.createNewFile();
        }
        out = new FileOutputStream(file);
        bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
        bw.write(s);
        bw.flush();
        bw.close();

    }
    public static void readFile(String strFile){
        try{
            InputStream is = new FileInputStream(PREFIX+strFile);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            String s=new String(bytes);
            System.out.println(s);
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //writeToFile("test.txt","hello1");
        readFile("test.txt");
    }
}
