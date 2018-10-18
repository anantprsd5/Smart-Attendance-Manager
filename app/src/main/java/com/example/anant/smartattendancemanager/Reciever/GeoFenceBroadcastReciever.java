package com.example.anant.smartattendancemanager.Reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GeoFenceBroadcastReciever extends BroadcastReceiver {

    List<String> mSubjects;
    private String UID;
    private DatabaseReference subjectsRef;

    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UID = user.getUid();
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
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map != null) {
                    final List<String> tableDataset = new ArrayList<>(map.keySet());
                    for (String key : tableDataset) {
                        mSubjects.add(map.get(key).toString().toLowerCase());
                        Log.d("checkID", map.get(key).toString());
                    }
                    getSubjectsList();
                    timeTableRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSubjectsList() {
        final String subjectsPath = "/users/" + UID + "/subjects/";
        final HashMap<String, Object> result = new HashMap<>();
        subjectsRef = FirebaseDatabase.getInstance().getReference(subjectsPath);
        subjectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map != null) {
                    final List<String> subjectsDataset = new ArrayList<>(map.keySet());
                    for (String subject : subjectsDataset) {
                        String classes = map.get(subject).toString();
                        int attended = Integer.parseInt(classes.substring(0, classes.indexOf("/")));
                        int noOfClasses = Integer.parseInt(classes.substring(classes.indexOf("/") + 1, classes.length()));
                        if (mSubjects.contains(subject.toLowerCase())) {
                            attended++;
                            noOfClasses++;
                        }
                        result.put(subject, attended + "/" + noOfClasses);
                    }
                }
                subjectsRef.updateChildren(result);
                subjectsRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
