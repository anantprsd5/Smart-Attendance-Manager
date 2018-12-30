package com.example.anant.smartattendancemanager.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SubjectsModel {

    String UID;
    private DatabaseReference ref;

    public interface OnDataSaved {
        void onSaved(boolean isSaved);
    }

    public SubjectsModel(String UID) {
        this.UID = UID;
        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("/users/" + UID + "/subjects");
    }

    public void saveSubjects(HashMap<String, String> mSubjectsMap, DatabaseReference reference) {
        try {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/users/" + UID + "/subjects", mSubjectsMap);
            reference.updateChildren(childUpdates);
        } catch (DatabaseException e) {

        }
    }

    public void saveSubjectsMain(OnDataSaved onDataSaved, HashMap<String, String> mSubjectsMap, DatabaseReference reference) {
        try {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/users/" + UID + "/subjects", mSubjectsMap);
            reference.updateChildren(childUpdates);
            onDataSaved.onSaved(true);
        } catch (DatabaseException e) {
            onDataSaved.onSaved(false);
        }
    }

    public void fetchSubjects(SubjectsFetched subjectsFetched) {
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

    public void updateChildren(HashMap<String, Object> result) {
        ref.updateChildren(result);
    }

    public void saveSingleSubjects(String subName, DatabaseReference reference) {
        reference.child(subName).setValue("0/0");
    }

    public void deleteSingleSubject(String subName, DatabaseReference reference) {
        reference.child(subName).removeValue();
    }

    public void getName(DatabaseReference reference, SubjectsFetched subjectsFetched){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjectsFetched.OnNameFetched(dataSnapshot.getValue().toString());
                reference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                reference.removeEventListener(this);
            }
        });
    }

}
