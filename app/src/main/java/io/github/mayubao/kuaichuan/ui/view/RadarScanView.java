package io.github.mayubao.kuaichuan.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import io.github.mayubao.kuaichuan.R;

/**
 * 自定义RadarView
 *
 * Created by mayubao on 2016/11/26.
 * Contact me 345269374@qq.com
 */
public class RadarScanView extends View {

    private static final int MSG_RUN = 1;

    private int mCircleColor = Color.BLACK;
    private int mLineColor = Color.BLACK;
    private int mArcColor = Color.WHITE;
    private int mArcStartColor = Color.WHITE;
    private int mArcEndColor = Color.TRANSPARENT;

    private Paint mCirclePaint; // 绘制圆形画笔
    private Paint mArcPaint; // 绘制扇形画笔
    private Paint mLinePaint; // 绘制线条画笔

    private RectF mRectF;

    private int mSweep; // 扇形角度

    public RadarScanView(Context context) {
        this(context, null);
    }

    public RadarScanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public RadarScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context){
        mCircleColor = context.getResources().getColor(R.color.transparent_white);
        mArcColor = context.getResources().getColor(R.color.transparent_white);
        mLineColor = context.getResources().getColor(R.color.transparent_white);

        mArcStartColor = context.getResources().getColor(R.color.transparent_white);
        mArcEndColor = context.getResources().getColor(android.R.color.transparent);


        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(1.f);

        mArcPaint.setColor(mArcColor);
        mArcPaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(1.f);

        mRectF = new RectF();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = getMeasuredWidth();
        setMeasuredDimension(size, size);
        mRectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

//        mArcPaint.setShader(new SweepGradient(size / 2, size / 2, Color.GRAY, Color.BLACK));
        mArcPaint.setShader(new SweepGradient(size / 2, size / 2, mArcStartColor, mArcEndColor));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;

        canvas.save();
        canvas.rotate(mSweep, centerX, centerY);
        canvas.drawArc(mRectF, 0, mSweep, true, mArcPaint);
        canvas.restore();

        canvas.drawLine(0, centerY, getMeasuredWidth(), centerY, mLinePaint);
        canvas.drawLine(centerX, 0, centerX, getMeasuredHeight(), mLinePaint);

        canvas.drawCircle(centerX, centerY, centerX / 2, mCirclePaint);
        canvas.drawCircle(centerX, centerY, centerX, mCirclePaint);
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_RUN) {
                mSweep+=4;
                if(mSweep > 360) mSweep = 0;
                postInvalidate();
//                sendEmptyMessage(MSG_RUN);
                sendEmptyMessageDelayed(MSG_RUN, 50);
            }
        }
    };

    /**
     * 对外公开扫描的方法
     */
    public void startScan(){
        if(mHandler != null){
            mHandler.obtainMessage(MSG_RUN).sendToTarget();
        }
    }
}
