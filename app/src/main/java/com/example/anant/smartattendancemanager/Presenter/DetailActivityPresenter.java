package com.example.anant.smartattendancemanager.Presenter;

import android.widget.EditText;
import android.widget.Toast;

import com.example.anant.smartattendancemanager.Model.AttendanceModel;
import com.example.anant.smartattendancemanager.Model.SubjectsFetched;
import com.example.anant.smartattendancemanager.Model.SubjectsModel;
import com.example.anant.smartattendancemanager.Model.TimeTableModel;
import com.example.anant.smartattendancemanager.R;
import com.example.anant.smartattendancemanager.View.DetailsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailActivityPresenter implements SubjectsFetched, AttendanceModel.OnAttendanceFetched {

    private DetailsView detailsView;
    private String day;

    public DetailActivityPresenter(DetailsView detailsView) {
        this.detailsView = detailsView;
    }

    public void fetchDayPosition() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        switch (day.toLowerCase()) {
            case "monday":
                detailsView.onDayPositionFetched(0);
                break;
            case "tuesday":
                detailsView.onDayPositionFetched(1);
                break;
            case "wednesday":
                detailsView.onDayPositionFetched(2);
                break;
            case "thursday":
                detailsView.onDayPositionFetched(3);
                break;
            case "friday":
                detailsView.onDayPositionFetched(4);
                break;
            case "saturday":
                detailsView.onDayPositionFetched(5);
                break;
            case "sunday":
                detailsView.onDayPositionFetched(6);
                break;
        }
    }

    public void checkLoggedIn(FirebaseAuth firebaseAuth) {
        if (firebaseAuth != null) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                detailsView.onLoginFailed();
            }
        } else {
            detailsView.onLoginFailed();
        }
    }

    @Override
    public void OnSubjectsFetched(Map<String, Object> map) {
        if (map != null) {
            ArrayList<String> subjects = new ArrayList<>(map.keySet());
            ArrayList<Integer> classAttended = new ArrayList<>();
            ArrayList<Integer> noOfClasses = new ArrayList<>();
            for (String subject : subjects) {
                String classes = map.get(subject).toString();
                int attended = Integer.parseInt(classes.substring(0, classes.indexOf("/")));
                int noOfClass = Integer.parseInt(classes.substring(classes.indexOf("/") + 1, classes.length()));
                classAttended.add(attended);
                noOfClasses.add(noOfClass);
            }

            detailsView.onSubjectsAttendanceFetched(subjects, classAttended, noOfClasses);
        } else detailsView.startMainActivity();
    }

    public void fetchSubjects(SubjectsModel subjectsModel) {
        subjectsModel.fetchSubjects(this);
    }

    public void fetchTimeTable(TimeTableModel timeTableModel, String day) {
        timeTableModel.getSubjectsForTimeTable(this, day);
    }

    public void updateAttendance(HashMap<String, Object> result, SubjectsModel subjectsModel) {
        subjectsModel.updateChildren(result);
    }

    public void fetchAttendanceCriteria(AttendanceModel attendanceModel) {
        attendanceModel.getAttendanceCriteria(this);
    }

    @Override
    public void attendanceFetched(int criteria) {
        detailsView.onAttendanceFetched(criteria);
    }

    public void saveData(String subjectName, DatabaseReference mDatabase, SubjectsModel subjectsModel) {
        if (subjectName.length() == 0) {
            return;
        } else {
            subjectsModel.saveSingleSubjects(subjectName, mDatabase);
        }
    }
}
