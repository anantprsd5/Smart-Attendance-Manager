package com.example.anant.smartattendancemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.example.anant.smartattendancemanager.R;
import com.ramotion.fluidslider.FluidSlider;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;

public class AttendanceActivity extends AppCompatActivity {

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.fluidSlider)
    FluidSlider fluidSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        ButterKnife.bind(this);

        final int max = 100;
        final int min = 0;
        final int total = max - min;

        // Java 8 lambda
        fluidSlider.setPositionListener(pos -> {
            final String value = String.valueOf((int) (min + total * pos));
            fluidSlider.setBubbleText(value);
            return Unit.INSTANCE;
        });

        fluidSlider.setPosition(0.3f);
        fluidSlider.setStartText(String.valueOf(min));
        fluidSlider.setEndText(String.valueOf(max));

        floatingActionButton.setOnClickListener(v -> startActivity(new Intent(AttendanceActivity.this, DetailActivity.class)));
    }
}
