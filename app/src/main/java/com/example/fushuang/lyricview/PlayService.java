package com.example.fushuang.lyricview;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener {


    private MediaPlayer mPlayer;
    private PlayBinder mBinder;
    private String TAG="111111111111111111";

    public PlayService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mBinder = new PlayBinder(mPlayer);
        mPlayer.setOnCompletionListener(this);
        Log.d(TAG, "onCreate: "+this);
        Log.d(TAG, "onCreate:   "+mPlayer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    class PlayBinder extends Binder{
        private MediaPlayer player;

        public PlayBinder(MediaPlayer player) {
            this.player = player;
        }

        public MediaPlayer getMediaPlayer(){
            return player;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

       return mBinder;
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
}
