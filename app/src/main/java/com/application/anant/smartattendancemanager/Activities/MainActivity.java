package com.application.anant.smartattendancemanager.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.anant.smartattendancemanager.Model.SubjectsModel;
import com.application.anant.smartattendancemanager.Presenter.MainActivityPresenter;
import com.application.anant.smartattendancemanager.R;
import com.application.anant.smartattendancemanager.View.AddSubjectsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AddSubjectsView {

    private static final String SUBJECTS_ADDED_PREF = "subPref";
    private LinearLayout linearLayout;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String UID;
    private MainActivityPresenter mainActivityPresenter;

    private int count = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

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
    public void onBackPressed() {
        subjectsAdded(false);
        if (count == 0) {
            count++;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            return;
        } else finishAffinity();
    }

    public void subjectsAdded(boolean added) {
        SharedPreferences.Editor editor = getSharedPreferences(SUBJECTS_ADDED_PREF, MODE_PRIVATE).edit();
        editor.putBoolean("subAdded", added);
        editor.apply();
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
        subjectsAdded(true);
        Intent intent = new Intent(this, TimeTableActivity.class);
        startActivity(intent);
    }
}
