package com.example.fushuang.lyricview;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, LyricView.LvCallback {

    private static final String TAG = "1111111111111";
    private MediaPlayer mMediaPlayer;
    private LyricView mLyricView;
    private MusicSearchResult.ShowapiResBodyBean.PagebeanBean.ContentlistBean been;
    private Thread mThread;
    private boolean isActivityExist=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mLyricView = (LyricView) findViewById(R.id.lricView);

        mLyricView.setCallback(this);

        bindService(new Intent(this, PlayService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mMediaPlayer = ((PlayService.PlayBinder) iBinder).getMediaPlayer();

                been = (MusicSearchResult.ShowapiResBodyBean.PagebeanBean.ContentlistBean) getIntent().getSerializableExtra("been");
                try {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                    }
                    mMediaPlayer.setOnPreparedListener(PlayActivity.this);
                    mMediaPlayer.setDataSource(PlayActivity.this, Uri.parse(been.getM4a()));
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, Service.BIND_AUTO_CREATE);





    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mMediaPlayer.start();
        RetrofitHelper.getInstance()
                .createApi(QQMusicService.class)
                .getLyricResult(been.getSongid(),31490,"274e07e744d7457bbc3e6c60682327e2")
                .enqueue(new Callback<LyricResult>() {
                    @Override
                    public void onResponse(Call<LyricResult> call, Response<LyricResult> response) {
                        String lyric = response.body().getShowapi_res_body().getLyric();
                        lyric = Html.fromHtml(lyric).toString();
                        lyric = lyric.replace("[", "\n\t[");
                        lyric.substring(1);
                        mLyricView.setLyric(lyric);
                        Log.d(TAG, "onResponse: "+lyric);

                        mThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (isActivityExist) {
                                    int currentPosition = mMediaPlayer.getCurrentPosition();
                                    mLyricView.setCurrentPosition(currentPosition);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        mThread.start();

                    }

                    @Override
                    public void onFailure(Call<LyricResult> call, Throwable t) {

                    }
                });



    }


    @Override
    public void getScrollToPosition(long position) {
        mMediaPlayer.seekTo((int) position);
    }


    @Override
    protected void onDestroy() {

        isActivityExist=false;
        super.onDestroy();

    }
}
