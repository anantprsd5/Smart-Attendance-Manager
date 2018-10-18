package com.example.anant.smartattendancemanager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SubectsHelper {

    private Context mContext;
    private String UID;
    private DatabaseReference subjectsRef;
    private Map<String, Object> map;

    public SubectsHelper(Context context) {
        mContext = context;
    }

    public Map<String, Object> getSubjetsMap() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UID = user.getUid();
        }
        final String subjectsPath = "/users/" + UID + "/subjects/";
        final HashMap<String, Object> result = new HashMap<>();
        subjectsRef = FirebaseDatabase.getInstance().getReference(subjectsPath);
        subjectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                map = (Map<String, Object>) dataSnapshot.getValue();
                if (map != null) {
                }
                subjectsRef.updateChildren(result);
                subjectsRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return map;
    }
}
