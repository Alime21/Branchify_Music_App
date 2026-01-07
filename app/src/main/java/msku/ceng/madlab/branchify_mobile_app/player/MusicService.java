package msku.ceng.madlab.branchify_mobile_app.player;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class MusicService extends Service {

    private MusicNotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = new MusicNotificationManager(this);
        notificationManager.createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MusicPlayerManager musicPlayerManager = MusicPlayerManager.getInstance();
        Song currentSong = musicPlayerManager.getCurrentSong();
        boolean isPlaying = musicPlayerManager.isPlaying();

        if (currentSong != null) {
            Notification notification = notificationManager.createNotification(currentSong, isPlaying);
            startForeground(MusicNotificationManager.NOTIFICATION_ID, notification);
        } else {
            stopForeground(true);
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}