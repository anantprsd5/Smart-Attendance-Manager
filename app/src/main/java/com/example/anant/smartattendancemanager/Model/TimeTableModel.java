package com.example.anant.smartattendancemanager.Model;

import com.example.anant.smartattendancemanager.Days;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TimeTableModel {

    private String UID;
    private SubjectsFetched subjectsFetched;

    public TimeTableModel(String UID, SubjectsFetched subjectsFetched) {
        this.UID = UID;
        this.subjectsFetched = subjectsFetched;
    }

    public void getSubjects() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/subjects");
        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map != null) {
                        subjectsFetched.OnSubjectsFetched(map);
                        ref.removeEventListener(this);
                    } else subjectsFetched.OnSubjectsFetched(null);
                } catch (ClassCastException e) {
                    subjectsFetched.OnSubjectsFetched(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                subjectsFetched.OnSubjectsFetched(null);
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void saveData(int position, HashMap<String, String> subjects,
                         DatabaseReference mDatabase) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + UID + "/table" + "/" + Days.values()[position], subjects);
        mDatabase.updateChildren(childUpdates);
    }
}
