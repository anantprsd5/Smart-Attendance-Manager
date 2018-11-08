package com.example.anant.smartattendancemanager.View;

import java.util.ArrayList;

public interface DetailsView {

    void onDayPositionFetched(int position);
    void onLoginFailed();
    void onSubjectsAttendanceFetched(ArrayList<String> subjects, ArrayList<Integer> attended,
                                     ArrayList<Integer> totalClass);
    void startMainActivity();
}
