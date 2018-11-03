package com.example.anant.smartattendancemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.anant.smartattendancemanager.Model.SubjectsModel;
import com.example.anant.smartattendancemanager.Presenter.MainActivityPresenter;
import com.example.anant.smartattendancemanager.R;
import com.example.anant.smartattendancemanager.View.AddSubjectsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AddSubjectsView {

    private LinearLayout linearLayout;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String UID;
    private MainActivityPresenter mainActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        UID = user.getUid();

        SubjectsModel subjectsModel = new SubjectsModel(UID);

        mainActivityPresenter = new MainActivityPresenter(this, UID, this,
                subjectsModel);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> mainActivityPresenter.saveData(mDatabase));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linearLayout = findViewById(R.id.subject_linear_layout);
        mainActivityPresenter.createEditTextView(linearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_subjects_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_subjects) {
            mainActivityPresenter.createEditTextView(linearLayout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataSaved() {
        Intent intent = new Intent(this, TimeTableActivity.class);
        startActivity(intent);
    }
}
