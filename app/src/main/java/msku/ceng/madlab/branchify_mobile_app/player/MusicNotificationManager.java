package msku.ceng.madlab.branchify_mobile_app.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.view.activities.MainActivity;

public class MusicNotificationManager {

    public static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "branchify_music_channel";

    private final Context context;
    private final NotificationManagerCompat notificationManager;

    public MusicNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Branchify Music Player",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification createNotification(Song song, boolean isPlaying) {
        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);

        // PendingIntents for actions
        PendingIntent playPauseIntent = createActionIntent(NotificationBroadcastReceiver.ACTION_PLAY_PAUSE);
        PendingIntent nextIntent = createActionIntent(NotificationBroadcastReceiver.ACTION_NEXT);
        PendingIntent prevIntent = createActionIntent(NotificationBroadcastReceiver.ACTION_PREVIOUS);

        // Create media style
        MediaStyle mediaStyle = new MediaStyle()
                .setShowActionsInCompactView(0, 1, 2); 

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(song.getTitle())
                .setContentText(song.getArtist())
                .setContentIntent(contentIntent)
                .setStyle(mediaStyle)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true);

        // Add actions
        builder.addAction(R.drawable.ic_previous, "Previous", prevIntent);
        if (isPlaying) {
            builder.addAction(R.drawable.ic_pause, "Pause", playPauseIntent);
        } else {
            builder.addAction(R.drawable.ic_play, "Play", playPauseIntent);
        }
        builder.addAction(R.drawable.ic_next, "Next", nextIntent);

        return builder.build();
    }

    private PendingIntent createActionIntent(String action) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    public void showNotification(Notification notification) {
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void hideNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}