package com.example.fushuang.lyricview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;


/**
 * Created by admin on 2017/6/6.
 */

public class LyricView extends View implements GestureDetector.OnGestureListener {

    private LyricInfo mLyricInfo;
    private int lineCount;
    private int mHeight;  //歌词控件的高度
    private int mWidth;    //歌词控件的宽度
    private TextPaint mPlayingPaint;  //绘制当前播放行的画笔
    private List<LineInfo> mLines;
    private int mIndex;      //当前播放下标
    private TextPaint mUnPlayingPaint;
    private float lineH;  //行高
    private float lineSP = 0;  //行间距
    private StaticLayout mStaticLayout;
    private GestureDetector mGestureDetector;
    private float scrollY = 0;
    private boolean isDrag = false;
    public LvCallback mCallback;
    private float offset = 1;   //歌词换行的平滑滚动,ValueAnimation 从1~0
    private boolean drawIndicator = false;
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mAnimator = ValueAnimator.ofFloat(1, 0);
                    mAnimator.setDuration(500);
                    mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            offset = (float) animation.getAnimatedValue();
                            postInvalidate();
                        }
                    });
                    mAnimator.start();
                    break;

                case 3:
                    drawIndicator = false;
                    scrollY = 0;
                    postInvalidate();
                    break;
            }
        }
    };
    private ValueAnimator mAnimator;

    private int playingTextColor;
    private float textSize;
    private float radius; // 半径
    private int unPlayingTextColor;
    private Paint mIndicatorPaint;  //指示器画笔
    private DashPathEffect mEffect;

    public LyricView(Context context) {
        super(context, null);
    }

    public LyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LyricView);
        playingTextColor = typedArray.getColor(R.styleable.LyricView_playingTextColor, Color.BLACK);
        unPlayingTextColor = typedArray.getColor(R.styleable.LyricView_unPlayingTextColor, Color.BLUE);
        textSize = typedArray.getDimension(R.styleable.LyricView_textSize, 50);
        radius = textSize;
        init();
        mGestureDetector = new GestureDetector(context, this);
    }

    public void setCallback(LvCallback callback) {
        mCallback = callback;
    }

    private void init() {

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setStrokeWidth(3);
        mIndicatorPaint.setColor(Color.RED);
        mEffect = new DashPathEffect(new float[]{10, 10}, 1);
        mIndicatorPaint.setPathEffect(mEffect);

        mIndicatorPaint.setStyle(Paint.Style.STROKE);

        mPlayingPaint = new TextPaint();
        mPlayingPaint.setColor(playingTextColor);
        mPlayingPaint.setTextSize(textSize);

        mUnPlayingPaint = new TextPaint();
        mUnPlayingPaint.setColor(unPlayingTextColor);
        mUnPlayingPaint.setTextSize(textSize);
    }


    public void setLyric(String lyric) {

        mLyricInfo = new LyricInfo();

        String[] split = lyric.split("\t");
        for (String line : split) {

            int index = line.lastIndexOf("]");

            if (line.startsWith("[offset:")) {
                // 时间偏移量
                String string = line.substring(8, index).trim();
                mLyricInfo.setSong_offset(Long.parseLong(string));
                continue;
            }
            if (line.startsWith("[ti:")) {
                // 标题
                String string = line.substring(4, index).trim();
                mLyricInfo.setSong_title(string);
                continue;
            }
            if (line.startsWith("[ar:")) {
                // 作者
                String string = line.substring(4, index).trim();
                mLyricInfo.setSong_artist(string);
                continue;
            }
            if (line.startsWith("[al:")) {
                // 所属专辑
                String string = line.substring(4, index).trim();
                mLyricInfo.setSong_album(string);
                continue;
            }
            if (line.startsWith("[by:")) {
                continue;
            }
            if (index == 9 && line.trim().length() > 10) {
                // 歌词内容
                LineInfo lineInfo = new LineInfo();
                lineInfo.setContent(line.substring(10, line.length()));
                lineInfo.setStart(measureStartTimeMillis(line.substring(0, 10)));
                mLyricInfo.addSong_lines(lineInfo);
            }
        }
        lineCount = mLyricInfo.getSong_lines().size();
        mLines = mLyricInfo.getSong_lines();
    }


    public void createStaticLayout(String content, TextPaint paint) {
        //StaticLayout 支持换行,drawText 存在不能换行和baseline 不在中间的问题
        mStaticLayout = new StaticLayout(content, paint, mWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawIndicator) {
            //画中间虚线
            mIndicatorPaint.setPathEffect(mEffect);
            Path path = new Path();
            path.moveTo(0, mHeight / 2);
            path.lineTo(mWidth - 2 * radius, mHeight / 2);
            canvas.drawPath(path, mIndicatorPaint);

            path.reset();

            mIndicatorPaint.setPathEffect(null);
            //绘制圆圈
            canvas.drawCircle(mWidth - radius, mHeight / 2, radius, mIndicatorPaint);

            //画出三角形
//          path.moveTo();
            canvas.drawLine(mWidth - radius * 1.5f, ((float) (mHeight / 2 - radius * Math.sqrt(3) / 2)), mWidth, mHeight / 2, mIndicatorPaint);
            canvas.drawLine(mWidth - radius * 1.5f, ((float) (mHeight / 2 - radius * Math.sqrt(3) / 2)), mWidth - radius * 1.5f, ((float) (mHeight / 2 + radius * Math.sqrt(3) / 2)), mIndicatorPaint);
            canvas.drawLine(mWidth, mHeight / 2, mWidth - radius * 1.5f, ((float) (mHeight / 2 + radius * Math.sqrt(3) / 2)), mIndicatorPaint);

        }

        canvas.translate(0, lineH * offset);
        canvas.translate(0, -scrollY);


        if (mLines != null && lineCount != 0) {
            createStaticLayout(mLines.get(mIndex).getContent().trim(), mPlayingPaint);
            lineH = mStaticLayout.getHeight();
            canvas.save();
            canvas.translate(0, (mHeight - lineH) / 2);
            mStaticLayout.draw(canvas);

            for (int i = mIndex + 1; i < lineCount; i++) {
                createStaticLayout(mLines.get(i).getContent(), mUnPlayingPaint);
                canvas.translate(0, lineSP + lineH);
                mStaticLayout.draw(canvas);
            }
            canvas.restore();

            canvas.save();

            canvas.translate(0, (mHeight - lineH) / 2);
            for (int i = mIndex - 1; i >= 0; i--) {

                createStaticLayout(mLines.get(i).getContent(), mUnPlayingPaint);
                canvas.translate(0, -(lineSP + lineH));
                mStaticLayout.draw(canvas);
            }
            canvas.restore();

        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    /**
     * 从字符串中获得时间值
     *
     * @param str 单行歌词开始部分时间字符串
     * @return 毫秒单位的时间值
     */
    private int measureStartTimeMillis(String str) {
        int minute = Integer.parseInt(str.substring(1, 3));
        int second = Integer.parseInt(str.substring(4, 6));
        int millisecond = Integer.parseInt(str.substring(7, 9));
        return millisecond + second * 1000 + minute * 60 * 1000;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrag = true;
//                scrollY = 0;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
//                float scrollLineCount = scrollY / lineH;
//                if (Math.abs(scrollLineCount) >= 1 && mCallback != null) {
//                    long start = mLines.get((int) (mIndex + scrollLineCount)).getStart();
//                    mCallback.getScrollToPosition(start);
//                }

//                scrollY = 0;
                isDrag = false;
                break;

        }

        mGestureDetector.onTouchEvent(event);

        return true;
    }

    public void setCurrentPosition(int position) {

        if (!isDrag && getCurrentLineIndex(position) != mIndex) {

            if (mLyricInfo != null) {
                mIndex = getCurrentLineIndex(position);
                offset = 1;
                mHandler.sendEmptyMessage(1);


            }
        }

    }

    public int getCurrentLineIndex(int position) {
        List<LineInfo> lines = mLyricInfo.getSong_lines();
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).getStart() > position) {
                return i - 1;
            }
        }
        return 0;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        if ((e.getX()-mWidth +radius) * (e.getX()-mWidth +radius) + (e.getY() - mHeight / 2) * (e.getY() - mHeight / 2) < radius * radius) {
            float scrollLineCount = scrollY / lineH;
            if (Math.abs(scrollLineCount) >= 1 && mCallback != null) {
                long start = mLines.get((int) (mIndex + scrollLineCount)).getStart();
                mCallback.getScrollToPosition(start);
                scrollY=0;
                postInvalidate();
            }

        }

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        drawIndicator = true;
        scrollY += distanceY;
        postInvalidate();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


    public interface LvCallback {
        void getScrollToPosition(long position);
    }

}
