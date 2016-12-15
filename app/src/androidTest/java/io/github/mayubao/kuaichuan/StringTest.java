package io.github.mayubao.kuaichuan;

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

    public static void main(String[] args){
        testReplace();
    }
}
