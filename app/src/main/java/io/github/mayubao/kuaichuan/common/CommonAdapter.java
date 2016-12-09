package io.github.mayubao.kuaichuan.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 共有的Adapter
 *
 * Created by mayubao on 2016/4/18.
 * Contact me 345269374@qq.com
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    Context mContext;
    List<T> mDataList;

    public CommonAdapter(Context context, List<T> dataList){
        this.mContext = context;
        this.mDataList = dataList;
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * 添加数据源
     * @param mDataList
     */
    public void addDataList(List<T> mDataList){
        this.mDataList.addAll(mDataList);
        notifyDataSetChanged();
    }

    /**
     * 清除数据
     */
    public void clear(){
        this.mDataList.clear();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_toolbar, null);
            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView) convertView.findViewById(R.id.tv_address);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        */
//        convertView = getConvertView();
        convertView = convertView(position, convertView);
        return convertView;
    }

    /**
     * 重写convertView方法
     *
     * @param position
     * @param convertView
     * @return
     */
    public abstract View convertView(int position, View convertView);

}
