package com.example.anant.smartattendancemanager.Activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anant.smartattendancemanager.Adapters.DetailsAdapter;
import com.example.anant.smartattendancemanager.AttendanceAppWidget;
import com.example.anant.smartattendancemanager.Helper.DatabaseHelper;
import com.example.anant.smartattendancemanager.Fragments.AttendanceDialogFragment;
import com.example.anant.smartattendancemanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements
        AttendanceDialogFragment.NoticeDialogListener, DatabaseHelper.OnDataFetchedListener {

    private LinearLayoutManager mLayoutManager;
    private String UID;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private DatabaseHelper helper;
    private boolean isTimeTable;
    private boolean updatedAttendance;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.days_backdrop)
    ImageView daysImageView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.no_subject_text_view)
    TextView noSubTextView;
    @BindView(R.id.recycler_view_details)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_title)
    Toolbar toolbar;

    private int[] days_backdrop = {R.drawable.monday_backdrop, R.drawable.tuesday_backdrop,
            R.drawable.wednesday_backdrop, R.drawable.thursday_backdrop,
            R.drawable.friday_backdrop, R.drawable.saturday_backdrop, R.drawable.sunday_backdrop};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getDayDrawable();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                startLoginActivity();
                return;
            }
        } else {
            startLoginActivity();
            return;
        }

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new

                LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        UID = user.getUid();

        if (isInternetConnected())
            navigationView.getMenu().getItem(0).setChecked(true);
        else navigationView.getMenu().getItem(1).setChecked(true);

        isTimeTable = true;
        updatedAttendance = false;

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.nav_time_table:
                                isTimeTable = true;
                                swipeRefreshLayout.setRefreshing(true);
                                helper.getTimeTable();
                                break;
                            case R.id.nav_logout:
                                startLoginActivity();
                                break;
                            case R.id.nav_all_subjects:
                                swipeRefreshLayout.setRefreshing(true);
                                isTimeTable = false;
                                helper.getSubjects();
                                break;
                        }
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("/users/" + UID + "/subjects");

        helper = new DatabaseHelper(UID, this);
        if (isInternetConnected()) {
            swipeRefreshLayout.setRefreshing(true);
            if (!isTimeTable)
                helper.getSubjects();
            else helper.getTimeTable();
        } else {
            setUpAdapter(helper.getSubjectFromSharedPreference(this));
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isInternetConnected())
                    if (!isTimeTable)
                        helper.getSubjects();
                    else helper.getTimeTable();
                else swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getDayDrawable() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        switch (day.toLowerCase()) {
            case "monday":
                daysImageView.setBackgroundResource(days_backdrop[0]);
                break;
            case "tuesday":
                daysImageView.setBackgroundResource(days_backdrop[1]);
                break;
            case "wednesday":
                daysImageView.setBackgroundResource(days_backdrop[2]);
                break;
            case "thursday":
                daysImageView.setBackgroundResource(days_backdrop[3]);
                break;
            case "friday":
                daysImageView.setBackgroundResource(days_backdrop[4]);
                break;
            case "saturday":
                daysImageView.setBackgroundResource(days_backdrop[5]);
                break;
            case "sunday":
                daysImageView.setBackgroundResource(days_backdrop[6]);
                break;
        }
    }

    private void startLoginActivity() {
        FirebaseAuth.getInstance().signOut();
        if (helper != null)
            helper.clearSharedPreference(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public void onDialogPositiveClick() {
        updatedAttendance = true;
        helper.getSubjects();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.logout) {
            startLoginActivity();
            return true;
        }

        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataFetched(Map<String, Object> map, boolean isSuccessful) {
        if (updatedAttendance && isTimeTable) {
            swipeRefreshLayout.setRefreshing(true);
            updatedAttendance = false;
            helper.getTimeTable();
            return;
        }
        if (map != null) {
            swipeRefreshLayout.setRefreshing(false);
            noSubTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            helper.addSubjectsToSharedPreference(map, DetailActivity.this);
            setUpAdapter(map);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setUpAdapter(Map<String, Object> map) {
        DetailsAdapter detailsAdapter = new DetailsAdapter(map, new DetailsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String key) {
                DialogFragment newFragment = new AttendanceDialogFragment();
                ((AttendanceDialogFragment) newFragment).setArguments(ref, key);
                newFragment.show(getSupportFragmentManager(), "attendance");
            }

            @Override
            public void onAttendanceMarked() {
                updatedAttendance = true;
                helper.getSubjects();
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        updateAppWidget();
        mRecyclerView.setAdapter(detailsAdapter);
    }

    private void updateAppWidget() {
        Intent intent = new Intent(this, AttendanceAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), AttendanceAppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null)
            isConnected = true;
        else
            isConnected = false;
        return isConnected;
    }
}
