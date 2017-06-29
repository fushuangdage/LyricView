package com.example.fushuang.lyricview;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private WaterView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (WaterView) findViewById(R.id.waterView);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.startWave();
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(5000);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float progress = (float) valueAnimator.getAnimatedValue();
                        Log.d("2222222222", "onAnimationUpdate: "+progress);
                        mView.setProgress(progress);
                    }
                });
                valueAnimator.start();
            }
        });
    }


}
