package com.example.rdvmanager;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.Nullable;

public class MusicService extends Service {
    MediaPlayer player;

    public class MyActivityBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    IBinder binder = new MyActivityBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                player = MediaPlayer.create(getApplicationContext(), R.raw.lofi);
                player.setLooping(true);
                player.start();
            }
        });
        t.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }


}
