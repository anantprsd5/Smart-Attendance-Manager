package com.example.anant.smartattendancemanager.Activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.anant.smartattendancemanager.Adapters.DetailsAdapter;
import com.example.anant.smartattendancemanager.AttendanceAppWidget;
import com.example.anant.smartattendancemanager.Fragments.AttendanceDialogFragment;
import com.example.anant.smartattendancemanager.Model.SubjectsModel;
import com.example.anant.smartattendancemanager.Presenter.DetailActivityPresenter;
import com.example.anant.smartattendancemanager.R;
import com.example.anant.smartattendancemanager.View.DetailsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements
        AttendanceDialogFragment.NoticeDialogListener,
        DetailsView, DetailsAdapter.OnItemClickListener {

    private LinearLayoutManager mLayoutManager;
    private String UID;
    private FirebaseAuth mAuth;
    private boolean isTimeTable;

    private boolean adapterSet;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.days_backdrop)
    ImageView daysImageView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.no_subject_scroll_view)
    NestedScrollView nestedScrollView;
    @BindView(R.id.recycler_view_details)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_title)
    Toolbar toolbar;

    private DetailActivityPresenter detailActivityPresenter;
    private DetailsAdapter detailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        detailActivityPresenter = new DetailActivityPresenter(this);
        detailActivityPresenter.fetchDayDrawable();
        detailActivityPresenter.checkLoggedIn(mAuth);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new

                LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        detailsAdapter = new DetailsAdapter(this);
        adapterSet = false;

        FirebaseUser user = mAuth.getCurrentUser();
        UID = user.getUid();

        if (isInternetConnected())
            navigationView.getMenu().getItem(0).setChecked(true);
        else navigationView.getMenu().getItem(1).setChecked(true);

        isTimeTable = false;

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    switch (menuItem.getItemId()) {
                        case R.id.nav_time_table:
                            isTimeTable = true;
                            swipeRefreshLayout.setRefreshing(true);
                            break;
                        case R.id.nav_logout:
                            startLoginActivity();
                            break;
                        case R.id.nav_all_subjects:
                            swipeRefreshLayout.setRefreshing(true);
                            isTimeTable = false;
                            break;
                    }
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here

                    return true;
                });

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        SubjectsModel subjectsModel = new SubjectsModel(UID);

        swipeRefreshLayout.setRefreshing(true);
        if (!isTimeTable)
            detailActivityPresenter.fetchSubjects(subjectsModel);
        else {

        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            detailActivityPresenter.fetchSubjects(subjectsModel);
        });
    }

    @Override
    public void onDialogPositiveClick() {

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

    @Override
    public void onDrawableFetched(int drawable, String day) {
        daysImageView.setBackgroundResource(drawable);
        daysImageView.setContentDescription(day);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterSet = false;
    }

    @Override
    public void onLoginFailed() {
        startLoginActivity();
        return;
    }

    @Override
    public void onSubjectsAttendanceFetched(ArrayList<String> subjects, ArrayList<Integer> attended, ArrayList<Integer> totalClass) {
        detailsAdapter.setDataset(subjects, attended, totalClass);
        if (!adapterSet) {
            adapterSet = true;
            mRecyclerView.setAdapter(detailsAdapter);
        }
        detailsAdapter.notifyDataSetChanged();
        updateAppWidget();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(String key) {

    }

    @Override
    public void onAttendanceMarked() {

    }
}
