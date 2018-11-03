package com.example.anant.smartattendancemanager.Presenter;

import com.example.anant.smartattendancemanager.Model.SubjectsFetched;
import com.example.anant.smartattendancemanager.Model.TimeTableModel;
import com.example.anant.smartattendancemanager.View.TimeTableView;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class TimeTablePresenter implements SubjectsFetched {

    private TimeTableModel timeTableModel;
    private TimeTableView timeTableView;

    public TimeTablePresenter(String UID, TimeTableView timeTableView) {
        timeTableModel = new TimeTableModel(UID, this);
        this.timeTableView = timeTableView;
    }

    public void listSubjects() {
        timeTableModel.getSubjects();
    }

    @Override
    public void OnSubjectsFetched(Map<String, Object> map) {
        timeTableView.onTableFetched(map);
    }

    public void saveData(int position, HashMap<String, String> subjects,
                         DatabaseReference mDatabase) {
        timeTableModel.saveData(position, subjects, mDatabase);
    }
}
