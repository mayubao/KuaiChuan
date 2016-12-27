package io.github.mayubao.kuaichuan.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.mayubao.kuaichuan.AppContext;
import io.github.mayubao.kuaichuan.Constant;
import io.github.mayubao.kuaichuan.R;
import io.github.mayubao.kuaichuan.common.BaseActivity;
import io.github.mayubao.kuaichuan.core.BaseTransfer;
import io.github.mayubao.kuaichuan.core.FileReceiver;
import io.github.mayubao.kuaichuan.core.entity.FileInfo;
import io.github.mayubao.kuaichuan.core.entity.IpPortInfo;
import io.github.mayubao.kuaichuan.core.utils.ApMgr;
import io.github.mayubao.kuaichuan.core.utils.FileUtils;
import io.github.mayubao.kuaichuan.core.utils.ToastUtils;
import io.github.mayubao.kuaichuan.ui.adapter.FileReceiverAdapter;

/**
 * 文件接收列表界面
 *
 * ReceiverWaitingActivity --->>> 文件接收列表界面
 *
 * 前提条件：
 * 1.文件发送方连接上文件接收方的局域网络(即为文件接收方的热点) 【TODO: 如何 文件接收方 收到 文件发送方的连接信息？ UDP？】
 *      如果是在文件发送UDP的话，那么应该在ReceiverWaitingActivity里面去监听
 * 2.
 *
 * Created by mayubao on 2016/11/28.
 * Contact me 345269374@qq.com
 */
public class FileReceiverActivity extends BaseActivity {

    private static final String TAG = FileReceiverActivity.class.getSimpleName();

    /**
     * Topbar相关UI
     */
    @Bind(R.id.tv_back)
    TextView tv_back;
    @Bind(R.id.tv_title)
    TextView tv_title;

    /**
     * 进度条 已传 耗时等UI组件
     */
    @Bind(R.id.pb_total)
    ProgressBar pb_total;
    @Bind(R.id.tv_value_storage)
    TextView tv_value_storage;
    @Bind(R.id.tv_unit_storage)
    TextView tv_unit_storage;
    @Bind(R.id.tv_value_time)
    TextView tv_value_time;
    @Bind(R.id.tv_unit_time)
    TextView tv_unit_time;


    /**
     * 扫描结果
     */
    @Bind(R.id.lv_result)
    ListView lv_result;

    FileReceiverAdapter mFileReceiverAdapter;
    FileInfo mCurFileInfo;

    IpPortInfo mIpPortInfo;

    ServerRunnable mReceiverServer;


    long mTotalLen = 0;     //所有总文件的进度
    long mCurOffset = 0;    //每次传送的偏移量
    long mLastUpdateLen = 0; //每个文件传送onProgress() 之前的进度
    String[] mStorageArray = null;


    long mTotalTime = 0;
    long mCurTimeOffset = 0;
    long mLastUpdateTime = 0;
    String[] mTimeArray = null;

    int mHasSendedFileCount = 0;

    public static final int MSG_FILE_RECEIVER_INIT_SUCCESS = 0X4444;
    public static final int MSG_ADD_FILE_INFO = 0X5555;
    public static final int MSG_UPDATE_FILE_INFO = 0X6666;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_FILE_RECEIVER_INIT_SUCCESS){
                sendMsgToFileSender(mIpPortInfo);
            }else if(msg.what == MSG_ADD_FILE_INFO){
                //ADD FileInfo 到 Adapter
                FileInfo fileInfo = (FileInfo) msg.obj;
                ToastUtils.show(getContext(), "收到一个任务：" + (fileInfo != null ? fileInfo.getFilePath() : ""));
            }else if(msg.what == MSG_UPDATE_FILE_INFO){
                //ADD FileInfo 到 Adapter
                updateTotalProgressView();
                if(mFileReceiverAdapter != null) mFileReceiverAdapter.update();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_receiver);

        ButterKnife.bind(this);

        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //关闭TCP UDP 资源
        //清除选中文件的信息
        //关闭热点

        if(mReceiverServer != null){
            mReceiverServer.close();
            mReceiverServer = null;
        }

        closeSocket();

        AppContext.getAppContext().getReceiverFileInfoMap().clear();

        ApMgr.disableAp(getContext());
        this.finish();
    }

    /**
     * 初始化
     */
    private void init(){
        //界面初始化
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getResources().getString(R.string.title_file_transfer));

        mFileReceiverAdapter = new FileReceiverAdapter(getContext());
        lv_result.setAdapter(mFileReceiverAdapter);

        mIpPortInfo = (IpPortInfo) getIntent().getSerializableExtra(Constant.KEY_IP_PORT_INFO);


        //Android6.0 requires android.permission.READ_EXTERNAL_STORAGE
        //TODO
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_FILE);
        }else{
            initServer(); //启动接收服务
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_WRITE_FILE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initServer(); //启动接收服务
            } else {
                // Permission Denied
                ToastUtils.show(this, getResources().getString(R.string.tip_permission_denied_and_not_receive_file));
                onBackPressed();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 开启文件接收端服务
     */
    private void initServer() {
        mReceiverServer = new ServerRunnable(Constant.DEFAULT_SERVER_PORT);
        new Thread(mReceiverServer).start();
    }

    /**
     * 更新进度 和 耗时的 View
     */
    private void updateTotalProgressView() {
        try{
            //设置传送的总容量大小
            mStorageArray = FileUtils.getFileSizeArrayStr(mTotalLen);
            tv_value_storage.setText(mStorageArray[0]);
            tv_unit_storage.setText(mStorageArray[1]);

            //设置传送的时间情况
            mTimeArray = FileUtils.getTimeByArrayStr(mTotalTime);
            tv_value_time.setText(mTimeArray[0]);
            tv_unit_time.setText(mTimeArray[1]);


            //设置传送的进度条情况
            if(mHasSendedFileCount == AppContext.getAppContext().getReceiverFileInfoMap().size()){
                pb_total.setProgress(0);
                tv_value_storage.setTextColor(getResources().getColor(R.color.color_yellow));
                tv_value_time.setTextColor(getResources().getColor(R.color.color_yellow));
                return;
            }

            long total = AppContext.getAppContext().getAllReceiverFileInfoSize();
            int percent = (int)(mTotalLen * 100 /  total);
            pb_total.setProgress(percent);

            if(total  == mTotalLen){
                pb_total.setProgress(0);
                tv_value_storage.setTextColor(getResources().getColor(R.color.color_yellow));
                tv_value_time.setTextColor(getResources().getColor(R.color.color_yellow));
            }
        }catch (Exception e){
            //convert storage array has some problem
        }
    }

    @OnClick({R.id.tv_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_back:{
                onBackPressed();
                break;
            }
        }
    }

    public void sendMsgToFileSender(final IpPortInfo ipPortInfo){
        new Thread(){
            @Override
            public void run() {
                try {
                    sendFileReceiverInitSuccessMsgToFileSender(ipPortInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 通知文件发送方 ===>>> 文件接收方初始化完毕
     */

    DatagramSocket mDatagramSocket;
    public void sendFileReceiverInitSuccessMsgToFileSender(IpPortInfo ipPortInfo) throws Exception{
        Log.i(TAG, "sendFileReceiverInitSuccessMsgToFileSender------>>>start");
        mDatagramSocket = new DatagramSocket(ipPortInfo.getPort() +1);
        byte[] receiveData = new byte[1024];
        byte[] sendData = null;
        InetAddress ipAddress = ipPortInfo.getInetAddress();
        //1.发送 文件接收方 初始化
        sendData = Constant.MSG_FILE_RECEIVER_INIT_SUCCESS.getBytes(BaseTransfer.UTF_8);
        DatagramPacket sendPacket =
                new DatagramPacket(sendData, sendData.length, ipAddress, ipPortInfo.getPort());
        mDatagramSocket.send(sendPacket);
        Log.i(TAG, "Send Msg To FileSender######>>>" + Constant.MSG_FILE_RECEIVER_INIT_SUCCESS);
        Log.i(TAG, "sendFileReceiverInitSuccessMsgToFileSender------>>>end");
    }

    /**
     * 关闭UDP Socket 流
     */
    private void closeSocket(){
        if(mDatagramSocket != null){
            mDatagramSocket.disconnect();
            mDatagramSocket.close();
            mDatagramSocket = null;
        }
    }


    /**
     * ServerSocket启动线程
     */
    class ServerRunnable implements Runnable{
        ServerSocket serverSocket;
        private int port;


        public ServerRunnable(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            Log.i(TAG, "------>>>Socket已经开启");
            try {
                serverSocket = new ServerSocket(Constant.DEFAULT_SERVER_PORT);
                mHandler.obtainMessage(MSG_FILE_RECEIVER_INIT_SUCCESS).sendToTarget();
                while (!Thread.currentThread().isInterrupted()){
                    Socket socket = serverSocket.accept();

                    //生成缩略图
                    FileReceiver fileReceiver = new FileReceiver(socket);
                    fileReceiver.setOnReceiveListener(new FileReceiver.OnReceiveListener() {
                        @Override
                        public void onStart() {
//                            handler.obtainMessage(MSG_SHOW_PROGRESS).sendToTarget();
                            mLastUpdateLen = 0;
                            mLastUpdateTime = System.currentTimeMillis();
                        }

                        @Override
                        public void onGetFileInfo(FileInfo fileInfo) {
                            mHandler.obtainMessage(MSG_ADD_FILE_INFO, fileInfo).sendToTarget();
                            mCurFileInfo = fileInfo;
                            AppContext.getAppContext().addReceiverFileInfo(mCurFileInfo);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }

                        @Override
                        public void onGetScreenshot(Bitmap bitmap) {
//                            handler.obtainMessage(MSG_SHOW_PROGRESS, bitmap).sendToTarget();
                        }

                        @Override
                        public void onProgress(long progress, long total) {
                            //=====更新进度 流量 时间视图 start ====//
                            mCurOffset = progress - mLastUpdateLen > 0 ? progress - mLastUpdateLen : 0;
                            mTotalLen = mTotalLen + mCurOffset;
                            mLastUpdateLen = progress;

                            mCurTimeOffset = System.currentTimeMillis() - mLastUpdateTime > 0 ? System.currentTimeMillis() - mLastUpdateTime : 0;
                            mTotalTime = mTotalTime + mCurTimeOffset;
                            mLastUpdateTime = System.currentTimeMillis();
                            //=====更新进度 流量 时间视图 end ====//

                            mCurFileInfo.setProcceed(progress);
                            AppContext.getAppContext().updateReceiverFileInfo(mCurFileInfo);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }

                        @Override
                        public void onSuccess(FileInfo fileInfo) {
                            //=====更新进度 流量 时间视图 start ====//
                            mHasSendedFileCount ++;

                            mTotalLen = mTotalLen + (fileInfo.getSize() - mLastUpdateLen);
                            mLastUpdateLen = 0;
                            mLastUpdateTime = System.currentTimeMillis();
                            //=====更新进度 流量 时间视图 end ====//

                            fileInfo.setResult(FileInfo.FLAG_SUCCESS);
                            AppContext.getAppContext().updateReceiverFileInfo(fileInfo);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }

                        @Override
                        public void onFailure(Throwable t, FileInfo fileInfo) {
                            mHasSendedFileCount ++;//统计发送文件

                            fileInfo.setResult(FileInfo.FLAG_FAILURE);
                            AppContext.getAppContext().updateFileInfo(fileInfo);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }
                    });

//                    mFileReceiver = fileReceiver;
//                    new Thread(fileReceiver).start();
                    AppContext.getAppContext().MAIN_EXECUTOR.execute(fileReceiver);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        /**
         * 关闭Socket 通信 (避免端口占用)
         */
        public void close(){
            if(serverSocket != null){
                try {
                    serverSocket.close();
                    serverSocket = null;
                } catch (IOException e) {
                }
            }
        }
    }
}
