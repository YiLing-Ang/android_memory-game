package iss.ca.memgame;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.media.MediaPlayer;

public class MusicService extends Service {

    private final IBinder mBinder = new ServiceBinder();
    private int musicLength = 0;
    private MediaPlayer player=null;


    public MusicService() {
    }

    public class ServiceBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onCreate() {
        player =MediaPlayer.create(this, R.raw.bgm);
        player.setLooping(true);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getStringExtra("class").equals("main")) {
            player.start();
            return Service.START_NOT_STICKY;
        }
        return Service.START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        if(player != null)
        {
            try{
                player.release();
            }finally {
                player = null;
            }
        }
    }

}

