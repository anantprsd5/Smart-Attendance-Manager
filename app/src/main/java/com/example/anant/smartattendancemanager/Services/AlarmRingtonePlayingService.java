package com.example.anant.smartattendancemanager.Services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

public class AlarmRingtonePlayingService extends Service {
    Ringtone ringtoneAlarm;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtoneAlarm = RingtoneManager.getRingtone(getApplicationContext(), alarmTone);
        ringtoneAlarm.setStreamType(AudioManager.STREAM_ALARM);
        ringtoneAlarm.play();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        ringtoneAlarm.stop();
    }
}
