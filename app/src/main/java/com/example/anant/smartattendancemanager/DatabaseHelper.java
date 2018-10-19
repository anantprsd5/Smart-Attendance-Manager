package com.example.anant.smartattendancemanager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DatabaseHelper {

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
}
