package io.github.mayubao.kuaichuan.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;

import io.github.mayubao.kuaichuan.Constant;
import io.github.mayubao.kuaichuan.core.utils.FileUtils;
import io.github.mayubao.kuaichuan.ui.ChooseFileActivity;
import io.github.mayubao.kuaichuan.ui.ChooseReceiverActivity;
import io.github.mayubao.kuaichuan.ui.FileReceiverActivity;
import io.github.mayubao.kuaichuan.ui.FileSenderActivity;
import io.github.mayubao.kuaichuan.ui.ReceiverWaitingActivity;
import io.github.mayubao.kuaichuan.ui.WebTransferActivity;

/**
 * UI导航的工具类
 *
 * Created by mayubao on 2016/11/25.
 * Contact me 345269374@qq.com
 */
public class NavigatorUtils {

    /**
     * 跳转到文件选择UI
     * @param context
     * @param isWebTransfer 是否要网页传
     */
    public static void toChooseFileUI(Context context, boolean isWebTransfer){
        if(context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, ChooseFileActivity.class);
        intent.putExtra(Constant.KEY_WEB_TRANSFER_FLAG, isWebTransfer);
        context.startActivity(intent);
    }

    /**
     * 跳转到选择文件UI
     * @param context
     */
    public static void toChooseFileUI(Context context){
        toChooseFileUI(context, false);
    }

    /**
     * 跳转到选择文件接受者UI
     * @param context
     */
    //    ChooseReceiverActivity
    public static void toChooseReceiverUI(Context context){
        if(context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, ChooseReceiverActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到选择文件接受者UI
     * @param context
     */
    //    ReceiverWaitingActivity
    public static void toReceiverWaitingUI(Context context){
        if(context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, ReceiverWaitingActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到文件发送列表UI
     * @param context
     */
    public static void toFileSenderListUI(Context context){
        if(context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, FileSenderActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到文件接收列表UI
     * @param context
     */
    public static void toFileReceiverListUI(Context context, Bundle bundle){
        if(context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, FileReceiverActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 打开指定的App文件存储文件夹
     * @param context
     */
    public static void toSystemFileChooser(Context context){
        if(context == null) {
            throw new RuntimeException("Context not be null!!!");
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        File file = new File(FileUtils.getRootDirPath());
        Uri uri = Uri.fromFile(file);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "*/*");
        context.startActivity(intent);
    }


    /**
     * 跳转到网页传UI
     * @param context
     */
    public static void toWebTransferUI(Context context){
        if(context == null) {
            throw new RuntimeException("Context not be null!!!");
        }

        Intent intent = new Intent(context, WebTransferActivity.class);
        context.startActivity(intent);
    }

}
