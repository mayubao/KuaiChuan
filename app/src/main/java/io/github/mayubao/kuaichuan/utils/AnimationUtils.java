package io.github.mayubao.kuaichuan.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 动画的工具类
 *
 * Created by mayubao on 2016/4/21.
 * Contact me 345269374@qq.com
 */
public class AnimationUtils {

    /**
     * 创建动画层
     * @param activity
     * @return
     */
    public static ViewGroup createAnimLayout(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
//        animLayout.setId(Integer.MAX_VALUE);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    /**
     * 添加任务动画
     *
     * @param activity
     * @param startView     起始view
     * @param targetView    目标view
     */
    public static void setAddTaskAnimation(Activity activity, View startView, View targetView, final AddTaskAnimationListener listener){
        //1.创建遮罩动画层
        ViewGroup animMaskLayout = createAnimLayout(activity);
        final ImageView imageView = new ImageView(activity);
        animMaskLayout.addView(imageView);

        //2.创建Animation
        int[] startLocArray = new int[2];
        int[] endLocArray = new int[2];
        startView.getLocationInWindow(startLocArray);
        targetView.getLocationInWindow(endLocArray);

        //3.设置遮罩层ImageView的LayoutParams
        ViewGroup.LayoutParams startViewLayoutParams = startView.getLayoutParams();
        ViewGroup.LayoutParams targetViewLayoutParams = targetView.getLayoutParams();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                startViewLayoutParams.width,
                startViewLayoutParams.height);
        lp.leftMargin = startLocArray[0];
        lp.topMargin = startLocArray[1];
        imageView.setLayoutParams(lp);

//        imageView.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_launcher));
        //设置遮罩层ImageView的背景
        if(startView != null && (startView instanceof ImageView)){
            ImageView iv = (ImageView) startView;
            imageView.setImageDrawable(iv.getDrawable() == null ? null : iv.getDrawable());
        }

        // 计算位移
        int xOffset = endLocArray[0] - startLocArray[0] + targetViewLayoutParams.width / 2;// 动画位移的X坐标
        int yOffset = endLocArray[1] - startLocArray[1] + targetViewLayoutParams.height / 2;// 动画位移的y坐标
        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                xOffset, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                0, yOffset);
        translateAnimationY.setInterpolator(new LinearInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.2f, 1.0f, 0.2f);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(800);// 动画的执行时间
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (listener != null) {
                    listener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(set);
    }


    /**
     * 购物车动画的监听
     */
    public interface AddTaskAnimationListener {

        void onAnimationStart(Animation animation);

        void onAnimationEnd(Animation animation);
    }


}
