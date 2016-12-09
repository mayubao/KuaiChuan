package io.github.mayubao.kuaichuan.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 选中文件列表改变的BroadReciver
 *
 * Created by mayubao on 2016/12/4.
 * Contact me 345269374@qq.com
 */
public abstract class SeletedFileListChangedBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = SeletedFileListChangedBroadcastReceiver.class.getSimpleName();
    //更新选择传送文件的Action
    public static final String ACTION_CHOOSE_FILE_LIST_CHANGED = "ACTION_CHOOSE_FILE_LIST_CHANGED";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(ACTION_CHOOSE_FILE_LIST_CHANGED)){ //选中传送的文件改变
            Log.i(TAG, "ACTION_CHOOSE_FILE_LIST_CHANGED--->>>");
            onSeletecdFileListChanged();
        }
    }

    /**
     * 选中传送的文件改变
     */
    public abstract void onSeletecdFileListChanged();
}
