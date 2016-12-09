package io.github.mayubao.kuaichuan.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by mayubao on 2016/12/7.
 * Contact me 345269374@qq.com
 */
public class MyScrollView extends ScrollView{

    private OnScrollListener mOnScrollListener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if(mOnScrollListener != null){//添加滚动监听
            this.mOnScrollListener.onScrollChanged(l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnScrollListener(OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;
    }

    /**
     * 滚动监听
     */
    public interface OnScrollListener{
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }


}
