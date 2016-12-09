package io.github.mayubao.kuaichuan.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by mayubao on 2016/11/26.
 * Contact me 345269374@qq.com
 */
public class SelectorTextView extends TextView {
    public SelectorTextView(Context context) {
        super(context);
    }

    public SelectorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 重写TextView 让其有按下的效果
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                break;
            }case MotionEvent.ACTION_UP:{
                break;
            }
        }

        return super.onTouchEvent(event);
    }
}
