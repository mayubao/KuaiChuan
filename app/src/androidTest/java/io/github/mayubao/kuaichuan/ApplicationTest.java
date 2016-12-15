package io.github.mayubao.kuaichuan;

import android.app.Application;
import android.test.ApplicationTestCase;

import io.github.mayubao.kuaichuan.core.utils.FileUtils;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


    public void testGetFileSizeStr(){
        System.out.println(FileUtils.getFileSize(1023));
        System.out.println(FileUtils.getFileSize(1023*1024 +23));
        System.out.println(FileUtils.getFileSize(1023*1024*1024 + 1023*1024));
        System.out.println(FileUtils.getFileSize(1023 * 1024 * 1024 * 1024l + 1023 * 1024 * 1024l));
    }


    public static void testReplace(){
        String string = "<h4>{app_name}</h4><h4>{app_name}</h4><h4>{app_name}</h4><h4>{app_name}</h4><h4>{app_name}</h4>";
        String newString = string.replaceAll("\\{app_name\\}", "fuckyou");
        System.out.println(newString);
    }

    public static  void main(String[] args){
        testReplace();
    }

}