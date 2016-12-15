package io.github.mayubao.kuaichuan.micro_server;

import android.util.Log;

/**
 * android micro server logger
 *
 * you can specify DEBUG in the development environment and set DEBUG = true;
 * if you hope your SLog print nothing, you can set the variable DEBUG to false.
 *
 * Created by mayubao on 2016/12/14.
 * Contact me 345269374@qq.com
 */
public class SLog {

    /**
     * 是否DEBUG标识
     */
    public static boolean DEBUG = true;

    public static void v(String tag, String msg){
        if(!DEBUG){
            return;
        }
        Log.v(tag, msg);
    }

    public static void d(String tag, String msg){
        if(!DEBUG){
            return;
        }
        Log.d(tag, msg);
    }


    public static void i(String tag, String msg){
        if(!DEBUG){
            return;
        }
        Log.i(tag, msg);
    }



    public static void w(String tag, String msg){
        if(!DEBUG){
            return;
        }
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg){
        if(!DEBUG){
            return;
        }
        Log.e(tag, msg);
    }
}
