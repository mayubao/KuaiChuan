package io.github.mayubao.kuaichuan.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.github.mayubao.kuaichuan.R;
import io.github.mayubao.kuaichuan.utils.StatusBarUtils;

/**
 * Created by mayubao on 2016/11/24.
 * Contact me 345269374@qq.com
 */
public class BaseActivity extends AppCompatActivity {


    /**
     * 写文件的请求码
     */
    public static final int  REQUEST_CODE_WRITE_FILE = 200;

    /**
     * 读取文件的请求码
     */
    public static final int  REQUEST_CODE_READ_FILE = 201;

    /**
     * 打开GPS的请求码
     */
    public static final int  REQUEST_CODE_OPEN_GPS = 205;

    Context mContext;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        StatusBarUtils.setStatuBarAndBottomBarTranslucent(this);
        super.onCreate(savedInstanceState);

    }


    /**
     * 获取上下文
     * @return
     */
    public Context getContext(){
        return mContext;
    }

    /**
     * 显示对话框
     */
    protected void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(mContext);
        }
        mProgressDialog.setMessage(getResources().getString(R.string.tip_loading));
        mProgressDialog.show();
    }

    /**
     * 隐藏对话框
     */
    protected void hideProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
            mProgressDialog = null;
        }
    }


}
