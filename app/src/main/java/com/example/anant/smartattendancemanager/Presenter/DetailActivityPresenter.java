package com.example.anant.smartattendancemanager.Presenter;

import com.example.anant.smartattendancemanager.Model.SubjectsFetched;
import com.example.anant.smartattendancemanager.Model.SubjectsModel;
import com.example.anant.smartattendancemanager.Model.TimeTableModel;
import com.example.anant.smartattendancemanager.R;
import com.example.anant.smartattendancemanager.View.DetailsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailActivityPresenter implements SubjectsFetched {

    private int[] days_backdrop = {R.drawable.monday_backdrop, R.drawable.tuesday_backdrop,
            R.drawable.wednesday_backdrop, R.drawable.thursday_backdrop,
            R.drawable.friday_backdrop, R.drawable.saturday_backdrop, R.drawable.sunday_backdrop};

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
        }
        else detailsView.startMainActivity();
    }

    public void fetchSubjects(SubjectsModel subjectsModel) {
        subjectsModel.fetchSubjects(this);
    }

    public void fetchTimeTable(TimeTableModel timeTableModel, String day){
        timeTableModel.fetchTimeTable(this, day);
    }

    public void updateAttendance(HashMap<String, Object> result, SubjectsModel subjectsModel){
        subjectsModel.updateChildren(result);
    }

    public void updateAttendance(HashMap<String, Object> result, TimeTableModel timeTableModel){
        timeTableModel.updateChildren(result, day);
    }
}
