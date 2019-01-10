package com.application.anant.smartattendancemanager.Model;

import android.util.Log;

import com.application.anant.smartattendancemanager.Days;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeTableModel {

    private String UID;

    public TimeTableModel(String UID) {
        this.UID = UID;
    }

    public void getSubjects(SubjectsFetched subjectsFetched) {
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

    public void fetchTimeTable(SubjectsFetched subjectsFetched, String day, Map<String, Object> subjects) {
        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> updatedTable = new HashMap<>();
        DatabaseReference ref = database.getReference("/users/" + UID + "/table" +
                "/" + day.toUpperCase());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map != null) {
                        final List<String> subjectDataset = new ArrayList<>(map.keySet());
                        for (String subject : subjectDataset) {
                            if (subjects.containsKey(subject)) {
                                updatedTable.put(subject, subjects.get(subject));
                                Log.wtf("SubjectsList", subject);
                            }
                        }
                        subjectsFetched.OnSubjectsFetched(updatedTable);
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

    public void getSubjectsForTimeTable(SubjectsFetched subjectsFetched, String day) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/subjects");
        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map != null) {
                        fetchTimeTable(subjectsFetched, day, map);
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
}
