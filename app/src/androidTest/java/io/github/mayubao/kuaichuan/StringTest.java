package io.github.mayubao.kuaichuan;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by mayubao on 2016/12/14.
 * Contact me 345269374@qq.com
 */
public class StringTest {


    public static void testReplace(){
        String string = "<h4>{app_name}</h4><h4>{app_name}</h4><h4>{app_name}</h4><h4>{app_name}</h4><h4>{app_name}</h4>";
        String newString = string.replaceAll("\\{app_name\\}", "fuckyou");
        System.out.println(newString);
    }

    public static void testUrlEncoder(){
        String englishStr = "helloworld";
        String chineseStr = "手机淘宝.apk";
        String eEncodeStr = null;
        String cEncodeStr = null;

        String eDecodeStr = null;
        String cDecodeStr = null;
        try {
            eEncodeStr = URLEncoder.encode(englishStr, "UTF-8");
            cEncodeStr = URLEncoder.encode(chineseStr, "UTF-8");
            System.out.println("eEncodeStr------>>>" + eEncodeStr);
            System.out.println("cEncodeStr------>>>" + cEncodeStr);

            eDecodeStr = URLDecoder.decode(eEncodeStr, "UTF-8");
            cDecodeStr = URLDecoder.decode(cEncodeStr, "UTF-8");
            System.out.println("eDecodeStr------>>>" + eDecodeStr);
            System.out.println("cDecodeStr------>>>" + cDecodeStr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void testURLEncode2(){
        String str1 = "%E6%89%8B%E6%9C%BA%E6%B7%98%E5%AE%9D.apk";
        String str2 = "P60811-095912.jpg";


        try {
            String eDecodeStr = URLDecoder.decode(str1, "UTF-8");
            String cDecodeStr = URLDecoder.decode(str2, "UTF-8");
            System.out.println("eDecodeStr------>>>" + eDecodeStr);
            System.out.println("cDecodeStr------>>>" + cDecodeStr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    public static void testSubString(){
        String filePath = "myapk.apk.png";
        String fileName = filePath.substring(0, filePath.length() - 4);
        System.out.println("fileName------>>>" + fileName);
    }

    public static void main(String[] args){
//        testReplace();
//        testUrlEncoder();
//        testURLEncode2();
        testSubString();
    }
}

