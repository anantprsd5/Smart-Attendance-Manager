package com.example.anant.smartattendancemanager.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.anant.smartattendancemanager.R;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class WidgetHelper {

    private final Context mContext;
    private int lowestPercentage;
    private int classAttended;
    private int classConducted;
    float percentage;

    public WidgetHelper(Context context) {
        mContext = context;
    }

    public Map<String, Object> getSubjectFromSharedPreference(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getString(R.string.attendance_shared_pref),
                MODE_PRIVATE);
        Map<String, Object> subjectsMap = new HashMap<>();
        Map<String, ?> stringMap = preferences.getAll();
        for (String i : stringMap.keySet())
            subjectsMap.put(i, stringMap.get(i));
        return subjectsMap;
    }

    public String getLowAttendanceSubject() {
        float percentage;
        int attended;
        int noOfClasses;
        String lowAttendanceSubject;
        float lowest;
        Map<String, Object> map = getSubjectFromSharedPreference(mContext);
        if (map != null && map.size() > 0) {
            lowAttendanceSubject = map.keySet().iterator().next();
            String classes = map.get(lowAttendanceSubject).toString();
            attended = Integer.parseInt(classes.substring(0, classes.indexOf("/")));
            noOfClasses = Integer.parseInt(classes.substring(classes.indexOf("/") + 1, classes.length()));
            if (noOfClasses != 0) {
                lowest = (float) attended / noOfClasses * 100;
            } else lowest = 0;

            for (String key : map.keySet()) {
                String percentageString = map.get(key).toString();
                attended = Integer.parseInt(percentageString.substring(0, percentageString.indexOf("/")));
                noOfClasses = Integer.parseInt(percentageString.substring(percentageString.indexOf("/") + 1, percentageString.length()));
                if (noOfClasses != 0)
                    percentage = ((float) attended) / noOfClasses * 100;
                else percentage = 0;
                if (percentage <= lowest) {
                    lowest = percentage;
                    setLowAttendancePercentage(lowest);
                    setClassAttended(attended);
                    setClassConducted(noOfClasses);
                    lowAttendanceSubject = key;
                }
            }
        } else lowAttendanceSubject = null;
        return lowAttendanceSubject;
    }

    public void setLowAttendancePercentage(float percentage) {
        this.percentage = percentage;
        lowestPercentage = Math.round(percentage);
    }

    public int getLowestPercentage() {
        return lowestPercentage;
    }

    public void setClassAttended(int attended) {
        classAttended = attended;
    }

    public int getClassAttended() {
        return classAttended;
    }

    public void setClassConducted(int conducted) {
        classConducted = conducted;
    }

    public int getClassConducted() {
        return classConducted;
    }

    public int getCanLeaveClasses() {
        int i = 0;
        while (percentage >= 75) {
            classConducted++;
            percentage = ((float) classAttended) / classConducted * 100;
            i++;
        }
        return i;
    }
}
