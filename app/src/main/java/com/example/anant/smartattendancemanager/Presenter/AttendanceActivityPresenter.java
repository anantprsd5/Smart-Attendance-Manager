package com.example.anant.smartattendancemanager.Presenter;

import com.example.anant.smartattendancemanager.Model.AttendanceModel;
import com.example.anant.smartattendancemanager.View.AttendanceView;
import com.google.firebase.database.DatabaseReference;
import com.ramotion.fluidslider.FluidSlider;

import java.util.HashMap;

import kotlin.Unit;

public class AttendanceActivityPresenter {

    private AttendanceView attendanceView;
    private AttendanceModel attendanceModel;

    public AttendanceActivityPresenter(AttendanceView attendanceView, AttendanceModel attendanceModel) {
        this.attendanceView = attendanceView;
        this.attendanceModel = attendanceModel;
    }

    public void setSlider(FluidSlider fluidSlider, float criteria) {
        final int max = 100;
        final int min = 10;
        final int total = max - min;

        // Java 8 lambda
        fluidSlider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (min + total * pos));
            fluidSlider.setBubbleText(value);
            attendanceView.onCriteriaSelected(value);
            return Unit.INSTANCE;
        });

        fluidSlider.setPosition(criteria);
        fluidSlider.setStartText(String.valueOf(min));
        fluidSlider.setEndText(String.valueOf(max));
    }

    public void setCriteria(String value, DatabaseReference databaseReference) {
        HashMap<String, String> criteria = new HashMap<>();
        criteria.put("criteria", value);
        attendanceModel.saveAttendanceCriteria(criteria, databaseReference);
    }
}
