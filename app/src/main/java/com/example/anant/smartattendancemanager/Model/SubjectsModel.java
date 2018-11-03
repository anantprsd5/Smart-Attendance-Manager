package com.example.anant.smartattendancemanager.Model;

import com.google.firebase.database.DatabaseReference;

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

}
