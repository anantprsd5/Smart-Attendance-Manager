package com.example.anant.smartattendancemanager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHelper {

    private String UID;
    private OnDataFetchedListener mOnDataFetchedListener;
    private Context mContext;

    public interface OnDataFetchedListener {
        void onDataFetched(Map<String, Object> map, boolean isSuccessful);
    }

    public DatabaseHelper(Context context, String UID, OnDataFetchedListener onDataFetchedListener) {
        this.UID = UID;
        mOnDataFetchedListener = onDataFetchedListener;
        mContext = context;
    }

    public void getSubjects() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/subjects");
        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
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

    public void addSubjectsToSharedPreference(Map<String, Object> map) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(mContext.getString(R.string.attendance_shared_pref),
                MODE_PRIVATE).edit();
        ArrayList<String> subjectDataset = new ArrayList<>(map.keySet());
        for (String subject : subjectDataset) {
            editor.putString(subject, map.get(subject).toString());
        }
        editor.apply();
    }

    public Map<String, Object> getSubjectFromSharedPreference() {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getString(R.string.attendance_shared_pref),
                MODE_PRIVATE);
        Map<String, Object> subjectsMap = new HashMap<>();
        Map<String, ?> stringMap = preferences.getAll();
        for (String i : stringMap.keySet())
            subjectsMap.put(i, stringMap.get(i));
        return subjectsMap;
    }
}
