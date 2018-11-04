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

    public void fetchDayDrawable() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        switch (day.toLowerCase()) {
            case "monday":
                detailsView.onDrawableFetched(days_backdrop[0], day);
                break;
            case "tuesday":
                detailsView.onDrawableFetched(days_backdrop[1], day);
                break;
            case "wednesday":
                detailsView.onDrawableFetched(days_backdrop[2], day);
                break;
            case "thursday":
                detailsView.onDrawableFetched(days_backdrop[3], day);
                break;
            case "friday":
                detailsView.onDrawableFetched(days_backdrop[4], day);
                break;
            case "saturday":
                detailsView.onDrawableFetched(days_backdrop[5], day);
                break;
            case "sunday":
                detailsView.onDrawableFetched(days_backdrop[6], day);
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

    public void fetchTimeTable(TimeTableModel timeTableModel){
        timeTableModel.fetchTimeTable(this, day);
    }

    public void updateAttendance(HashMap<String, Object> result, SubjectsModel subjectsModel){
        subjectsModel.updateChildren(result);
    }
}
