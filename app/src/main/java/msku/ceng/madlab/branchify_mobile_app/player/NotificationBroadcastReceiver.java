package msku.ceng.madlab.branchify_mobile_app.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_PLAY_PAUSE = "msku.ceng.madlab.branchify.PLAY_PAUSE";
    public static final String ACTION_NEXT = "msku.ceng.madlab.branchify.NEXT";
    public static final String ACTION_PREVIOUS = "msku.ceng.madlab.branchify.PREVIOUS";

    @Override
    public void onReceive(Context context, Intent intent) {
        MusicPlayerManager musicPlayerManager = MusicPlayerManager.getInstance();

        switch (intent.getAction()) {
            case ACTION_PLAY_PAUSE:
                if (musicPlayerManager.isPlaying()) {
                    musicPlayerManager.pause();
                } else {
                    musicPlayerManager.resume();
                }
                break;
            case ACTION_NEXT:
                musicPlayerManager.next();
                break;
            case ACTION_PREVIOUS:
                musicPlayerManager.previous();
                break;
        }
    }
}