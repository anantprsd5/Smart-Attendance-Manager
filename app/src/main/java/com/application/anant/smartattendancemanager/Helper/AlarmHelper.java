package com.application.anant.smartattendancemanager.Helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.application.anant.smartattendancemanager.Reciever.AlarmReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class AlarmHelper {

    private Context mContext;
    private int hour;
    private int minute;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public AlarmHelper(Context context, int hour, int minute) {
        mContext = context;
        this.hour = hour;
        this.minute = minute;
    }

    public void setAlarm() {
        alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        Calendar morning = Calendar.getInstance();
        morning.set(Calendar.HOUR_OF_DAY, hour);
        morning.set(Calendar.MINUTE, minute);
        morning.set(Calendar.SECOND, 0);
        morning.set(Calendar.MILLISECOND, 0);
        Intent myIntent = new Intent(mContext, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(mContext, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, morning.getTimeInMillis(), pendingIntent);
    }
}
