package com.example.fushuang.lyricview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by admin on 2017/5/31.
 */

public class WaterView extends View {

    private static final String TAG = "1111111111111";
    private Paint mPaint; //水波纹画笔
    private Path mPath;  //水波纹路径
    private int mWaveHeight;  //水波纹高度
    private int mWL;  //屏幕宽度
    private int mFu = 200;  //波浪的振幅高度
    private int mOffset;  //水波纹偏移量
    private int mViewHeight;
    private Paint mTextPaint;
    private String mProgress="0%";

    public WaterView(Context context) {
        super(context, null);
    }

    public WaterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.parseColor("#5DCEC6"));
        mPaint.setAntiAlias(true);
        mPath = new Path();
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(75);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWL = getMeasuredWidth();
        mViewHeight=getMeasuredHeight();
    }

    public void startWave() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mWL);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mOffset = (int) valueAnimator.getAnimatedValue();
                Log.d(TAG, "onAnimationUpdate: " + mOffset);
                postInvalidate();
            }
        });
        valueAnimator.start();
    }

    public void setProgress(float progress){
        mWaveHeight= (int) (mViewHeight*progress);
        if (progress>0.5){
            mTextPaint.setColor(Color.WHITE);
        }else {
            mTextPaint.setColor(Color.parseColor("#5DCEC6"));
        }
        mProgress = ((int) (100 * progress))+ "%";

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        mPath.reset();
        int wh = mViewHeight - mWaveHeight; //水波平面的高度 ，向下为y轴正方向

        final int sc = canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), null, Canvas.ALL_SAVE_FLAG);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qq);//获取 bitmap 资源
//        canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2, getHeight() / 2 - bitmap.getHeight() / 2, null);//绘制 bitmap
        canvas.drawBitmap(bitmap, new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()), new RectF(0, (float) (getMeasuredHeight()*0.1),getMeasuredWidth(),getMeasuredHeight()), null);//绘制 bitmap
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//设置特效画笔

        mPath.moveTo(mWL + mOffset, wh);
        mPath.lineTo(mWL + mOffset, mViewHeight);
        mPath.lineTo(-mWL + mOffset, mViewHeight);
        mPath.lineTo(-mWL + mOffset, wh);
        mPath.quadTo(-mWL * 3 / 4 + mOffset, wh - mFu, -mWL / 2 + mOffset, wh);
        mPath.quadTo(-mWL / 4 + mOffset, wh + mFu, 0 + mOffset, wh);
        mPath.quadTo(mWL / 4 + mOffset, wh - mFu, mWL / 2 + mOffset, wh);
        mPath.quadTo(mWL * 3 / 4 + mOffset, wh + mFu, mWL + mOffset, wh);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
        Log.d(TAG, "onDraw: "+mWaveHeight);


        canvas.drawText(mProgress,getMeasuredWidth()/2-mTextPaint.measureText(mProgress)/2,getMeasuredHeight()/2,mTextPaint);
        canvas.restoreToCount(sc);

    }
}
