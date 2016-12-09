package io.github.mayubao.kuaichuan.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import io.github.mayubao.kuaichuan.R;
import io.github.mayubao.kuaichuan.common.BaseActivity;
import io.github.mayubao.kuaichuan.core.FileSender;
import io.github.mayubao.kuaichuan.core.entity.FileInfo;
import io.github.mayubao.kuaichuan.core.receiver.WifiBroadcastReceiver;
import io.github.mayubao.kuaichuan.core.utils.FileUtils;
import io.github.mayubao.kuaichuan.core.utils.ToastUtils;
import io.github.mayubao.kuaichuan.core.utils.WifiMgr;

/**
 * Created by mayubao on 2016/11/28.
 * Contact me 345269374@qq.com
 */
public class ClientActivity extends BaseActivity {

    private static final String TAG = ClientActivity.class.getSimpleName();
    public static final String DEFAULT_SSID = "XD_HOTSPOT";
    public static final int DEFAULT_PORT = 8080;

    private Button btn_send;
    private EditText et_content;

    private Context mContext;

    private Socket mSocket;

    private WifiBroadcastReceiver mWifiBroadcastReceiver;

    private boolean isFirst = true;

    private ProgressBar pb;
    private TextView tv_progress;
    private ImageView iv_shortcut;
    private Button btn_suspend;
    private Button btn_resume;

    private GridView gv;

    private List<FileInfo> mFileInfoList;

    private int index = 1;

    Thread mCurrentThread;
    FileSender mFileSender;


    Object obj = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        mContext = this;

        btn_send = (Button) this.findViewById(R.id.btn_send);
        et_content = (EditText) this.findViewById(R.id.et_content);

        pb = (ProgressBar) this.findViewById(R.id.pb);
        tv_progress = (TextView) this.findViewById(R.id.tv_progress);
        pb.setVisibility(View.GONE);
        tv_progress.setVisibility(View.GONE);

        btn_suspend = (Button) this.findViewById(R.id.btn_suspend);
        btn_resume = (Button) this.findViewById(R.id.btn_resume);

        gv = (GridView) this.findViewById(R.id.gv);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((v.getId())){
                    case R.id.btn_suspend:{
//                        if(mCurrentThread != null){
////                            mCurrentThread.suspend();
//                            try {
//                                mCurrentThread.wait();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }

                        if(mFileSender != null){
//                            try {
//                                mFileUSender.wait();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            mFileSender.pause();
                        }

                        break;
                    }
                    case R.id.btn_resume:{
//                        if(mCurrentThread != null){
////                            mCurrentThread.resume();
//                            mCurrentThread.notify();
//                        }

                        if(mFileSender != null){
                            mFileSender.resume();
                        }
                        break;
                    }
                }
            }
        };

        btn_suspend.setOnClickListener(listener);
        btn_resume.setOnClickListener(listener);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //生成缩略图
                FileInfo fileInfo = mFileInfoList.get(index);
                String hotspotIpAddress = WifiMgr.getInstance(mContext).getIpAddressFromHotspot();
                final FileSender fileSender = new FileSender(mContext,fileInfo, hotspotIpAddress, DEFAULT_PORT);
                fileSender.setOnSendListener(new FileSender.OnSendListener() {
                    @Override
                    public void onStart() {
                        handler.obtainMessage(MSG_SHOW_PROGRESS).sendToTarget();
                    }

                    @Override
                    public void onProgress(long progress, long total) {
                        int percent = (int)(progress *  100 / total);
                        handler.obtainMessage(MSG_UPDATE, percent, 0).sendToTarget();
                    }

                    @Override
                    public void onSuccess(FileInfo fileInfo) {
                        handler.obtainMessage(MSG_HIDE_PROGRESS).sendToTarget();
                    }

                    @Override
                    public void onFailure(Throwable t, FileInfo fileInfo) {
                        handler.obtainMessage(MSG_ERROR, t).sendToTarget();
                    }
                });

                mFileSender = fileSender;
                mCurrentThread = new Thread(fileSender);
                mCurrentThread.start();

                index++;
            }
        });


        mWifiBroadcastReceiver = new WifiBroadcastReceiver() {
            @Override
            public void onWifiEnabled() {
                Log.i(TAG, "onWifiEnabled------>>>");
//                String hotspotIpAddress = WifiMgr.getInstance(mContext).getIpAddressFromHotspot();
//                mCommunicateRunnable = new CommunicateRunnable(hotspotIpAddress, DEFAULT_PORT);
//                new Thread(mCommunicateRunnable).start();
            }
        };
        IntentFilter filter = new IntentFilter(WifiBroadcastReceiver.ACTION_WIFI_STATE_CHANGED);
        registerReceiver(mWifiBroadcastReceiver, filter);

        WifiMgr.getInstance(mContext).openWifi();
        WifiMgr.getInstance(mContext).addNetwork(WifiMgr.createWifiCfg(DEFAULT_SSID, null, WifiMgr.WIFICIPHER_NOPASS));

        /**
        String hotspotIpAddress = WifiMgr.getInstance(mContext).getIpAddressFromHotspot();
        //如果未连接上Wifi网络是无法获取IP的
//        ToastUtils.show(mContext, "获取hotspotIpAddress--->>>" + hotspotIpAddress );
        new Thread(new CommunicateRunnable(hotspotIpAddress, DEFAULT_PORT)).start();
         */


        handler.post(new Runnable() {
            @Override
            public void run() {
                mFileInfoList = FileUtils.getSpecificTypeFiles(mContext, new String[]{FileInfo.EXTEND_APK});
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWifiBroadcastReceiver != null){
            unregisterReceiver(mWifiBroadcastReceiver);
            mWifiBroadcastReceiver = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_apk) {
            getTask(FileInfo.TYPE_APK);
            return true;
        }else if (id == R.id.action_jpg) {
            getTask(FileInfo.TYPE_JPG);
            return true;
        }else if (id == R.id.action_mp3) {
            getTask(FileInfo.TYPE_MP3);
            return true;
        }else if (id == R.id.action_mp4) {
            getTask(FileInfo.TYPE_MP4);
            return true;
        }else if(id == R.id.action_settings){

            new Thread(){
                @Override
                public void run() {
                    try {
                        receiveServerUdpInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }



        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void getTask(final int type){
        new Thread(){
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog();
                    }
                });

                List<FileInfo> fileInfos = null;
                switch (type){
                    case FileInfo.TYPE_APK:{
                        fileInfos = FileUtils.getSpecificTypeFiles(mContext, new String[]{".apk"});
                        Log.i("APKFILE", "file count======>>>");
                        for(FileInfo fileInfo : fileInfos){
                            Log.i("APKFILE", fileInfo.toString());
                        }
                        break;
                    }

                    case FileInfo.TYPE_JPG:{
                        fileInfos = FileUtils.getSpecificTypeFiles(mContext, new String[]{".jpeg", ".jpg"});
                        Log.i("PICFILE", "file count======>>>");
                        for(FileInfo fileInfo : fileInfos){
                            Log.i("PICFILE", fileInfo.toString());
                        }
                        break;
                    }
                    case FileInfo.TYPE_MP3:{
                        fileInfos = FileUtils.getSpecificTypeFiles(mContext, new String[]{".mp3"});
                        Log.i("MP3FILE", "file count======>>>");
                        for(FileInfo fileInfo : fileInfos){
                            Log.i("MP3FILE", fileInfo.toString());
                        }
                        break;
                    }
                    case FileInfo.TYPE_MP4:{
                        fileInfos = FileUtils.getSpecificTypeFiles(mContext, new String[]{".mp4", ".rmvb"});
                        Log.i("MP4FILE", "file count======>>>");
                        for(FileInfo fileInfo : fileInfos){
                            Log.i("MP4FILE", fileInfo.toString());
                        }
                        break;
                    }
                }

                mFileInfoList = fileInfos;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                    }
                });
            }
        }.start();
    }

    public static final int MSG_UPDATE = 0X00001;
    public static final int MSG_SHOW_PROGRESS = 0X00002;
    public static final int MSG_HIDE_PROGRESS = 0X00003;
    public static final int MSG_ERROR = 0X00004;

    ProgressDialog pd;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {


            if(msg.what == MSG_UPDATE){
                int progress = msg.arg1;
                pb.setProgress(progress);
                tv_progress.setText(progress + "%");
            }else if(msg.what == MSG_SHOW_PROGRESS){
//                showDailog();
                pb.setVisibility(View.VISIBLE);
                tv_progress.setVisibility(View.VISIBLE);

                pb.setProgress(0);
                pb.setMax(100);
                tv_progress.setText("0%");
            }else if(msg.what == MSG_HIDE_PROGRESS){
                pb.setVisibility(View.GONE);
                tv_progress.setVisibility(View.GONE);
            }else if(msg.what == MSG_ERROR){
                Throwable t = (Throwable) msg.obj;
                ToastUtils.show(mContext, t.getCause().toString());
                ToastUtils.show(mContext, t.getMessage());
            }

            /*
            if(msg.what == MSG_SHOW_PROGRESS){
                showDailog();
            }else if(msg.what == MSG_HIDE_PROGRESS){
                dimissDailog();
            }else if(msg.what == 0x11){
                ToastUtils.show(mContext, "Client: SelectionKey is readable.");
            }else if(msg.what == 0x22){
                ToastUtils.show(mContext, "Client: SelectionKey is writable.");
            }
            super.handleMessage(msg);
            */
        }
    };

    public void showDailog(){
        if(pd == null){
            pd = new ProgressDialog(mContext);
            pd.setMessage(mContext.getResources().getString(R.string.str_loadding));
            pd.show();
        }
    }

    public void dimissDailog(){
        if(pd != null && pd.isShowing()){
            pd.dismiss();
            pd = null;
        }
    }


    private void receiveServerUdpInfo() throws Exception{
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while(true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String( receivePacket.getData());
            System.out.println("RECEIVED: " + sentence);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String capitalizedSentence = sentence.toUpperCase();
            sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
        }
    }

}

