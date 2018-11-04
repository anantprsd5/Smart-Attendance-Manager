package com.example.anant.smartattendancemanager.Model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SubjectsModel {

    String UID;

    public SubjectsModel(String UID) {
        this.UID = UID;
    }

    public void saveSubjects(HashMap<String, String> mSubjectsMap, DatabaseReference reference) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + UID + "/subjects", mSubjectsMap);
        reference.updateChildren(childUpdates);
    }

    public void fetchSubjects(SubjectsFetched subjectsFetched) {
        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/users/" + UID + "/subjects");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map != null) {
                        subjectsFetched.OnSubjectsFetched(map);
                    } else
                        subjectsFetched.OnSubjectsFetched(null);
                } catch (ClassCastException e) {
                    subjectsFetched.OnSubjectsFetched(null);
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                subjectsFetched.OnSubjectsFetched(null);
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

}
