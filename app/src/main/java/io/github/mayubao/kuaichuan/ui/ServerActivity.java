package io.github.mayubao.kuaichuan.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import io.github.mayubao.kuaichuan.R;
import io.github.mayubao.kuaichuan.common.BaseActivity;
import io.github.mayubao.kuaichuan.core.FileReceiver;
import io.github.mayubao.kuaichuan.core.entity.FileInfo;
import io.github.mayubao.kuaichuan.core.receiver.WifiAPBroadcastReceiver;
import io.github.mayubao.kuaichuan.core.utils.ApMgr;
import io.github.mayubao.kuaichuan.core.utils.WifiMgr;

/**
 * Created by mayubao on 2016/11/28.
 * Contact me 345269374@qq.com
 */
public class ServerActivity extends BaseActivity {

    private static final String TAG = ServerActivity.class.getSimpleName();
    public static final String DEFAULT_SSID = "XD_HOTSPOT";
    public static final int DEFAULT_PORT = 8080;

    private Context mContext;

    private LinearLayout ll_server;
    private Button btn_start;
    private Button btn_stop;

//    private ServerBroadcastReceiver mServerBroadcastReceiver;

    private WifiAPBroadcastReceiver mWifiAPBroadcastReceiver;

    private ServerRunnable mServerRunnable;


    private ProgressBar pb;
    private TextView tv_progress;
    private ImageView iv_shortcut;
    private ImageView iv_big;

    private Button btn_suspend;
    private Button btn_resume;

    FileReceiver mFileReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        mContext = this;

        ll_server = (LinearLayout) this.findViewById(R.id.ll_server);
        btn_start = (Button) this.findViewById(R.id.btn_start);
        btn_stop = (Button) this.findViewById(R.id.btn_stop);
        btn_start.setOnClickListener(mOnClickListener);
        btn_stop.setOnClickListener(mOnClickListener);

        pb = (ProgressBar) this.findViewById(R.id.pb);
        tv_progress = (TextView) this.findViewById(R.id.tv_progress);
        iv_shortcut = (ImageView) this.findViewById(R.id.iv_shortcut);
        iv_big = (ImageView) this.findViewById(R.id.iv_big);
        pb.setVisibility(View.GONE);
        tv_progress.setVisibility(View.GONE);
        iv_shortcut.setVisibility(View.GONE);

        btn_suspend = (Button) this.findViewById(R.id.btn_suspend);
        btn_resume = (Button) this.findViewById(R.id.btn_resume);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((v.getId())){
                    case R.id.btn_suspend:{

                        if(mFileReceiver != null){
                            mFileReceiver.pause();
                        }

                        break;
                    }
                    case R.id.btn_resume:{

                        if(mFileReceiver != null){
                            mFileReceiver.resume();
                        }
                        break;
                    }
                }
            }
        };

        btn_suspend.setOnClickListener(listener);
        btn_resume.setOnClickListener(listener);


        WifiMgr.getInstance(mContext).disableWifi();
//        new Thread(new ServerRunnable(DEFAULT_PORT)).start();
        if(ApMgr.isApOn(mContext)){
            ApMgr.disableAp(mContext);
        }

        mWifiAPBroadcastReceiver = new WifiAPBroadcastReceiver() {
            @Override
            public void onWifiApEnabled() {
                mServerRunnable = new ServerRunnable(DEFAULT_PORT);
                new Thread(mServerRunnable).start();
            }
        };
        IntentFilter filter = new IntentFilter(WifiAPBroadcastReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        registerReceiver(mWifiAPBroadcastReceiver, filter);


        ApMgr.isApOn(mContext); // check Ap state :boolean
        ApMgr.configApState(mContext, DEFAULT_SSID); // change Ap state :boolean
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_start:{
                    if(mServerRunnable != null) return;

                    mServerRunnable = new ServerRunnable(DEFAULT_PORT);
                    new Thread(mServerRunnable).start();
                    break;
                }
                case R.id.btn_stop:{
                    if(mServerRunnable != null){
                        mServerRunnable.close();
                        mServerRunnable = null;
                    }
                    break;
                }
            }
        }
    };

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
        if(mWifiAPBroadcastReceiver != null){
            unregisterReceiver(mWifiAPBroadcastReceiver);
        }

        if(mServerRunnable != null){
            mServerRunnable.close();
            mServerRunnable = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            new Thread(){
                @Override
                public void run() {
                    try {
                        sendWifiInfoToClient();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            return true;
        }

        return super.onOptionsItemSelected(item);
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
                serverSocket = new ServerSocket(DEFAULT_PORT);
                while (!Thread.currentThread().isInterrupted()){
                    Socket socket = serverSocket.accept();

                    //生成缩略图
                    FileReceiver fileReceiver = new FileReceiver(socket);
                    fileReceiver.setOnReceiveListener(new FileReceiver.OnReceiveListener() {
                        @Override
                        public void onStart() {
//                            handler.obtainMessage(MSG_SHOW_PROGRESS).sendToTarget();
                        }

                        @Override
                        public void onGetFileInfo(FileInfo fileInfo) {

                        }

                        @Override
                        public void onGetScreenshot(Bitmap bitmap) {
                            handler.obtainMessage(MSG_SHOW_PROGRESS, bitmap).sendToTarget();
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

                        }
                    });

                    mFileReceiver = fileReceiver;
                    new Thread(fileReceiver).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

    public static final int MSG_UPDATE = 0X00001;
    public static final int MSG_SHOW_PROGRESS = 0X00002;
    public static final int MSG_HIDE_PROGRESS = 0X00003;

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
                iv_shortcut.setVisibility(View.VISIBLE);

                pb.setProgress(0);
                pb.setMax(100);
                tv_progress.setText("0%");
                if(msg.obj != null){
                    Bitmap bitmap = (Bitmap) msg.obj;
                    iv_shortcut.setImageBitmap(bitmap);
                    iv_big.setImageBitmap(bitmap);
                }

            }else if(msg.what == MSG_HIDE_PROGRESS){
                pb.setVisibility(View.GONE);
                tv_progress.setVisibility(View.GONE);
                iv_shortcut.setVisibility(View.GONE);
            }


            /*
            if(msg.what == MSG_UPDATE){
                TextView tv = (TextView) View.inflate(mContext, R.layout.view_text, null);
                tv.setText((String)msg.obj);
                ll_server.addView(tv);
            }else if(msg.what == MSG_SHOW_PROGRESS){
                showDailog();
            }else if(msg.what == MSG_HIDE_PROGRESS){
                dimissDailog();
            }
            */

            super.handleMessage(msg);
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




    /*
    //==========================================================================================
    //Hotspot Receiver

    final class ServerBroadcastReceiver extends BroadcastReceiver{

        //WIFI AP state action
        public static final String ACTION_WIFI_AP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED";

        //WIFI state action
        public static final String ACTION_WIFI_STATE_CHANGED ="android.net.wifi.WIFI_STATE_CHANGED";
//        public static final String ACTION_WIFI_STATE_CHANGED2 ="android.net.wifi.STATE_CHANGE";



        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_WIFI_AP_STATE_CHANGED)){ //Wifi AP state changed
                // get Wi-Fi Hotspot state here
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.i(TAG, "Wifi Ap state--->>>" + state);
                if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                    // Wifi is enabled
                }
            }else if(action.equals(ACTION_WIFI_STATE_CHANGED)){//wifi state changed
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                    Log.e(TAG, " ----- Wifi  Disconnected ----- ");
                }else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    Log.e(TAG, " ----- Wifi  Connected ----- ");
                }

            }
        }
    }

    //==========================================================================================
    */


    private void sendWifiInfoToClient() throws Exception{

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        String sentence = "hello world";
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + modifiedSentence);
        clientSocket.close();
    }
}

