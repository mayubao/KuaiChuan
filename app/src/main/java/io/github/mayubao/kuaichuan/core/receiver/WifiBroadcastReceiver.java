package io.github.mayubao.kuaichuan.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Wifi BroadReciver
 * wifi 广播
 * 
 * Created by mayubao on 2016/11/4.
 * Contact me 345269374@qq.com
 */
public abstract class WifiBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = WifiBroadcastReceiver.class.getSimpleName();
    //WIFI state action
    public static final String ACTION_WIFI_STATE_CHANGED ="android.net.wifi.WIFI_STATE_CHANGED";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(ACTION_WIFI_STATE_CHANGED)){//wifi state changed
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                Log.i(TAG, " ----- Wifi  Disconnected ----- ");
            }else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                Log.i(TAG, " ----- Wifi  Connected ----- ");
                onWifiEnabled();
            }

        }
    }

    /**
     * Wifi已经连接上的回调
     */
    public abstract void onWifiEnabled();
}
