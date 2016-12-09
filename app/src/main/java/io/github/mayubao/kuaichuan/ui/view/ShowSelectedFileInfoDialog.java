package io.github.mayubao.kuaichuan.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.mayubao.kuaichuan.AppContext;
import io.github.mayubao.kuaichuan.R;
import io.github.mayubao.kuaichuan.core.entity.FileInfo;
import io.github.mayubao.kuaichuan.core.receiver.SeletedFileListChangedBroadcastReceiver;
import io.github.mayubao.kuaichuan.core.utils.FileUtils;
import io.github.mayubao.kuaichuan.ui.adapter.FileInfoSeletedAdapter;

/**
 * 显示选中的文件列表对话框
 *
 * Created by mayubao on 2016/12/2.
 * Contact me 345269374@qq.com
 */
public class ShowSelectedFileInfoDialog {

    /**
     * UI控件
     */
    @Bind(R.id.btn_operation)
    Button btn_operation;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.lv_result)
    ListView lv_result;

    Context mContext;
    AlertDialog mAlertDialog;
    FileInfoSeletedAdapter mFileInfoSeletedAdapter;

    public ShowSelectedFileInfoDialog(Context context) {
        this.mContext = context;

        View contentView = View.inflate(mContext, R.layout.view_show_selected_file_info_dialog, null);

        ButterKnife.bind(this, contentView);

        String title = getAllSelectedFilesDes();
        tv_title.setText(title);

        mFileInfoSeletedAdapter = new FileInfoSeletedAdapter(mContext);
        mFileInfoSeletedAdapter.setOnDataListChangedListener(new FileInfoSeletedAdapter.OnDataListChangedListener() {
            @Override
            public void onDataChanged() {
                if(mFileInfoSeletedAdapter.getCount() == 0){
                    hide();
                }
                tv_title.setText(getAllSelectedFilesDes());
                sendUpdateSeletedFilesBR();//发送更新选中文件的广播
            }
        });

        lv_result.setAdapter(mFileInfoSeletedAdapter);

        this.mAlertDialog = new AlertDialog.Builder(mContext)
                                .setView(contentView)
                                .create();
    }

    /**
     * //发送更新选中文件列表的广播
     */
    private void sendUpdateSeletedFilesBR(){
        mContext.sendBroadcast(new Intent(SeletedFileListChangedBroadcastReceiver.ACTION_CHOOSE_FILE_LIST_CHANGED));
    }


    @OnClick({R.id.btn_operation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_operation: {
                clearAllSelectedFiles();
                sendUpdateSeletedFilesBR();//发送更新选中文件的广播
                break;
            }
        }
    }

    /**
     * 获取选中文件对话框的Title
     * @return
     */
    private String getAllSelectedFilesDes(){
        String title = "";

        long totalSize = 0;
        Set<Map.Entry<String,FileInfo>> entrySet = AppContext.getAppContext().getFileInfoMap().entrySet();
        for(Map.Entry<String,FileInfo> entry : entrySet){
            FileInfo fileInfo = entry.getValue();
            totalSize = totalSize + fileInfo.getSize();
        }

        title = mContext.getResources().getString(R.string.str_selected_file_info_detail)
                .replace("{count}", String.valueOf(entrySet.size()))
                .replace("{size}", String.valueOf(FileUtils.getFileSize(totalSize)));

        return title;
    }

    /**
     * 清除所有选中的文件
     */
    private void clearAllSelectedFiles(){
        AppContext.getAppContext().getFileInfoMap().clear();
        if(mFileInfoSeletedAdapter != null){
            mFileInfoSeletedAdapter.notifyDataSetChanged();
        }

        this.hide();
    }

    /**
     * 显示
     */
    public void show(){
        if(this.mAlertDialog != null){
            notifyDataSetChanged();
            tv_title.setText(getAllSelectedFilesDes());
            this.mAlertDialog.show();
        }
    }

    /**
     * 隐藏
     */
    public void hide(){
        if(this.mAlertDialog != null){
            this.mAlertDialog.hide();
        }
    }


    /**
     * 通知列表发生变化
     */
    public void notifyDataSetChanged(){
        if(mFileInfoSeletedAdapter != null){
            mFileInfoSeletedAdapter.notifyDataSetChanged();
        }
    }
}
