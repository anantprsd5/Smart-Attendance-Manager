package com.example.anant.smartattendancemanager.View;

import java.util.ArrayList;

public interface DetailsView {

    void onDrawableFetched(int drawable, String day);
    void onLoginFailed();
    void onSubjectsAttendanceFetched(ArrayList<String> subjects, ArrayList<Integer> attended,
                                     ArrayList<Integer> totalClass);
    void startMainActivity();
}
