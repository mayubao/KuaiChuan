package io.github.mayubao.kuaichuan.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Wifi AP BroadReciver
 * wifi 热点 广播
 *
 * Created by mayubao on 2016/11/4.
 * Contact me 345269374@qq.com
 */
public abstract class WifiAPBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = WifiAPBroadcastReceiver.class.getSimpleName();
    //WIFI AP state action
    public static final String ACTION_WIFI_AP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(ACTION_WIFI_AP_STATE_CHANGED)){ //Wifi AP state changed
            // get Wi-Fi Hotspot state here
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.i(TAG, "Wifi Ap state--->>>" + state);
            if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                // Wifi is enabled
                onWifiApEnabled();
            }else if(WifiManager.WIFI_STATE_ENABLING == state % 10){
                // Wifi is enabling
            }else if(WifiManager.WIFI_STATE_DISABLED == state % 10){
                // Wifi is disable
            }else if(WifiManager.WIFI_STATE_DISABLING == state % 10){
                // Wifi is disabling
            }
        }
    }

    /**
     * 热点可用
     */
    public abstract void onWifiApEnabled();
}
