package com.example.anant.smartattendancemanager.Reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.anant.smartattendancemanager.Activities.StopAlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.wtf("AlarmRec", "Recived");
        Intent intentone = new Intent(context, StopAlarmActivity.class);
        intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentone);
    }
}
