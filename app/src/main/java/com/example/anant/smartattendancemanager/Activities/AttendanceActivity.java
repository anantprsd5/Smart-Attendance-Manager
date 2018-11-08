package com.example.anant.smartattendancemanager.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.anant.smartattendancemanager.R;
import com.ramotion.fluidslider.FluidSlider;

import kotlin.Unit;

public class AttendanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        final int max = 100;
        final int min = 0;
        final int total = max - min;

        final FluidSlider slider = findViewById(R.id.fluidSlider);

        // Java 8 lambda
        slider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (min + total * pos));
            slider.setBubbleText(value);
            return Unit.INSTANCE;
        });

        slider.setPosition(0.3f);
        slider.setStartText(String.valueOf(min));
        slider.setEndText(String.valueOf(max));
    }
}
