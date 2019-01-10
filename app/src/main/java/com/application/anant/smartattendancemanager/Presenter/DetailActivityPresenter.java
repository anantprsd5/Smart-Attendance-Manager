package com.application.anant.smartattendancemanager.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;

import com.application.anant.smartattendancemanager.Model.AttendanceModel;
import com.application.anant.smartattendancemanager.Model.SubjectsFetched;
import com.application.anant.smartattendancemanager.Model.SubjectsModel;
import com.application.anant.smartattendancemanager.Model.TimeTableModel;
import com.application.anant.smartattendancemanager.NavigationTypeFace;
import com.application.anant.smartattendancemanager.R;
import com.application.anant.smartattendancemanager.View.DetailsView;
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

import static android.content.Context.MODE_PRIVATE;

public class DetailActivityPresenter implements SubjectsFetched, AttendanceModel.OnAttendanceFetched {

    private DetailsView detailsView;
    private String day;
    private Paint p = new Paint();

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
            try {
                for (String subject : subjects) {
                    String classes = map.get(subject).toString();
                    int attended = Integer.parseInt(classes.substring(0, classes.indexOf("/")));
                    int noOfClass = Integer.parseInt(classes.substring(classes.indexOf("/") + 1, classes.length()));
                    classAttended.add(attended);
                    noOfClasses.add(noOfClass);
                    detailsView.onSubjectsAttendanceFetched(subjects, classAttended, noOfClasses);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
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

    public void applyFontToMenuItem(MenuItem mi, Context context) {
        Typeface typeface = ResourcesCompat.getFont(context, R.font.product_sans);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new NavigationTypeFace("", typeface), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void toggleFirstVisit(Context context, boolean bool) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.first_visit), MODE_PRIVATE).edit();
        editor.putBoolean("isFirst", bool);
        editor.apply();
    }

    public boolean isFirstVisit(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.first_visit), MODE_PRIVATE);
        boolean value = prefs.getBoolean("isFirst", true);
        return value;
    }

    public boolean isFirstAttendanceView(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.first_visit), MODE_PRIVATE);
        boolean value = prefs.getBoolean(context.getString(R.string.first_visit_attendance), true);
        return value;
    }

    public void toggleFirstVisitAttendance(Context context, boolean bool) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.first_visit), MODE_PRIVATE).edit();
        editor.putBoolean(context.getString(R.string.first_visit_attendance), bool);
        editor.apply();
    }

    public void removeData(String subName, DatabaseReference reference, SubjectsModel subjectsModel) {
        subjectsModel.deleteSingleSubject(subName, reference);
    }

    public void fetchName(DatabaseReference reference, SubjectsModel subjectsModel){
        subjectsModel.getName(reference, this);
    }

    @Override
    public void OnNameFetched(String name) {
        detailsView.onNameFetched(name);
    }
}
