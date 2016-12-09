package io.github.mayubao.kuaichuan.ui.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.mayubao.kuaichuan.R;
import io.github.mayubao.kuaichuan.common.CommonAdapter;

/**
 * Wifi Scan Result Adapter
 *
 * Created by mayubao on 2016/11/28.
 * Contact me 345269374@qq.com
 */
public class WifiScanResultAdapter extends CommonAdapter<ScanResult> {

    public WifiScanResultAdapter(Context context, List<ScanResult> dataList) {
        super(context, dataList);
    }

    @Override
    public View convertView(int position, View convertView) {
        ScanResultHolder viewHolder = null;

        if(convertView == null){
            convertView = View.inflate(getContext(), R.layout.item_wifi_scan_result, null);
            viewHolder = new ScanResultHolder();
            viewHolder.iv_device = (ImageView) convertView.findViewById(R.id.iv_device);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_mac = (TextView) convertView.findViewById(R.id.tv_mac);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ScanResultHolder) convertView.getTag();
        }

        ScanResult scanResult = getDataList().get(position);
        if(scanResult != null){
            viewHolder.tv_name.setText(scanResult.SSID);
            viewHolder.tv_mac.setText(scanResult.BSSID);
        }

        return convertView;
    }

    static class ScanResultHolder {
        ImageView iv_device;
        TextView tv_name;
        TextView tv_mac;
    }
}
