package com.example.anant.smartattendancemanager.Model;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AttendanceModel {

    private String UID;
    private OnAttendanceFetched onAttendanceFetched;

    public AttendanceModel(String UID) {
        this.UID = UID;
    }

    public void saveAttendanceCriteria(HashMap<String, String> criteria, DatabaseReference mDatabase) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + UID + "/attendance", criteria);
        mDatabase.updateChildren(childUpdates);
    }

    public void getAttendanceCriteria(OnAttendanceFetched onAttendanceFetched) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/users/" + UID + "/attendance");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                onAttendanceFetched.attendanceFetched(Integer.parseInt(map.get("criteria").toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onAttendanceFetched.attendanceFetched(75);
            }
        });
    }

    public interface OnAttendanceFetched{
        void attendanceFetched(int criteria);
    }
}
