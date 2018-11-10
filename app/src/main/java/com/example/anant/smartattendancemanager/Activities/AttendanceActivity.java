package com.example.anant.smartattendancemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.anant.smartattendancemanager.Model.AttendanceModel;
import com.example.anant.smartattendancemanager.Presenter.AttendanceActivityPresenter;
import com.example.anant.smartattendancemanager.R;
import com.example.anant.smartattendancemanager.View.AttendanceView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ramotion.fluidslider.FluidSlider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttendanceActivity extends AppCompatActivity
        implements AttendanceView {

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.fluidSlider)
    FluidSlider fluidSlider;
    @BindView(R.id.close_button)
    ImageView closeButtonView;
    private AttendanceActivityPresenter attendanceActivityPresenter;
    private String UID;
    private String criteria;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        ButterKnife.bind(this);

        Toast.makeText(this, "Att activity started", Toast.LENGTH_SHORT).show();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        UID = user.getUid();

        int attendanceValue = getIntent().getIntExtra("criteria", 75);

        float value = (float) attendanceValue / 100;

        AttendanceModel attendanceModel = new AttendanceModel(UID);

        attendanceActivityPresenter = new AttendanceActivityPresenter(this, attendanceModel);
        attendanceActivityPresenter.setSlider(fluidSlider, value);

        closeButtonView.setOnClickListener(v -> onBackPressed());

        floatingActionButton.setOnClickListener(v -> {
            attendanceActivityPresenter.setCriteria(criteria, mDatabase);
            startActivity(new Intent(AttendanceActivity.this, DetailActivity.class));
        });
    }

    @Override
    public void onCriteriaSelected(String value) {
        criteria = value;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}
