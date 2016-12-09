package io.github.mayubao.kuaichuan.core.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Toast工具类
 *
 * Created by mayubao on 2016/11/3.
 * Contact me 345269374@qq.com
 */
public class ToastUtils {

    static Toast toast = null;
    public static void show(Context context, String text) {
        try {
            if(toast!=null){
                toast.setText(text);
            }else{
                toast= Toast.makeText(context, text, Toast.LENGTH_SHORT);
            }
            toast.show();
        } catch (Exception e) {//子线程中Toast异常情况处理
            Looper.prepare();
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }
}
