package com.example.anant.smartattendancemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHelper {

    private static Map<String, Object> mSubjectsMap;

    private String UID;
    private OnDataFetchedListener mOnDataFetchedListener;

    public interface OnDataFetchedListener {
        void onDataFetched(Map<String, Object> map, boolean isSuccessful);
    }

    public DatabaseHelper(String UID, OnDataFetchedListener onDataFetchedListener) {
        this.UID = UID;
        mOnDataFetchedListener = onDataFetchedListener;
    }

    public void getSubjects() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/subjects");
        Log.wtf("checkDatabase", "/users/" + UID + "/subjects");
        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    mSubjectsMap = map;
                    mOnDataFetchedListener.onDataFetched(map, true);
                    ref.removeEventListener(this);
                } catch (ClassCastException e) {
                    mOnDataFetchedListener.onDataFetched(null, false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnDataFetchedListener.onDataFetched(null, false);
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void addSubjectsToSharedPreference(Map<String, Object> map, Context mContext) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(mContext.getString(R.string.attendance_shared_pref),
                MODE_PRIVATE).edit();
        ArrayList<String> subjectDataset = new ArrayList<>(map.keySet());
        for (String subject : subjectDataset) {
            editor.putString(subject, map.get(subject).toString());
        }
        editor.apply();
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

    public void clearSharedPreference(Context mContext) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(mContext.getString(R.string.attendance_shared_pref),
                MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public void getTimeTable() {
        if (mSubjectsMap == null) {
            getSubjects();
        }
        String timeTablePath = "/users/" + UID + "/table/";
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        timeTablePath = timeTablePath + day.toUpperCase();
        final DatabaseReference timeTableRef = FirebaseDatabase.getInstance().getReference(timeTablePath);
        timeTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Object> arrayList = (ArrayList<Object>) dataSnapshot.getValue();
                if (arrayList == null) {
                    mOnDataFetchedListener.onDataFetched(null, true);
                    timeTableRef.removeEventListener(this);
                } else {
                    Map<String, Object> newMap = new HashMap<>();
                    for (Object key : arrayList.toArray()) {
                        if (key != null) {
                            if (mSubjectsMap.keySet().contains(key.toString())) {
                                newMap.put(key.toString(), mSubjectsMap.get(key));
                            }
                        }
                    }
                    mOnDataFetchedListener.onDataFetched(newMap, true);
                    timeTableRef.removeEventListener(this);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
