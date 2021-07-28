package Tests;

import java.io.FileReader;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.net.URL;
  
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;


public class TestJson {
    public static void TestJson() throws Exception
    {

        //URL url = getClass().getResource("ListStopWords.txt");
        //File file = new File(url.getPath());

        String filePath = "Config/test.json";
        File file = new File(filePath);
        //String path = file.getPath();
        //System.out.println("the path is" + path+"\n");
        //String absPath = file.getAbsolutePath();
        //System.out.println("the abs path is" +absPath +"\n");

        Object obj = new JSONParser().parse(new FileReader(file.getAbsolutePath()));

        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;

         // getting firstName and lastName
         String is = (String) jo.get("this");
         String test = (String) jo.get("a");

         System.out.println("this: " + is); 
         System.out.println("a: " + test); 
    }
}
