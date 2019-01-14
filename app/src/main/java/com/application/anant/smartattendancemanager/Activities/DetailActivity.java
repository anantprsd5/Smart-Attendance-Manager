package com.application.anant.smartattendancemanager.Activities;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.application.anant.smartattendancemanager.Adapters.DaysViewPagerAdapter;
import com.application.anant.smartattendancemanager.Adapters.DetailsAdapter;
import com.application.anant.smartattendancemanager.AttendanceAppWidget;
import com.application.anant.smartattendancemanager.Days;
import com.application.anant.smartattendancemanager.Fragments.AttendanceDialogFragment;
import com.application.anant.smartattendancemanager.Helper.DatabaseHelper;
import com.application.anant.smartattendancemanager.Helper.RecyclerItemTouchHelper;
import com.application.anant.smartattendancemanager.Model.AttendanceModel;
import com.application.anant.smartattendancemanager.Model.SubjectsModel;
import com.application.anant.smartattendancemanager.Model.TimeTableModel;
import com.application.anant.smartattendancemanager.Presenter.DetailActivityPresenter;
import com.application.anant.smartattendancemanager.R;
import com.application.anant.smartattendancemanager.View.DetailsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class DetailActivity extends AppCompatActivity implements
        AttendanceDialogFragment.NoticeDialogListener,
        DetailsView, DetailsAdapter.OnItemClickListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private LinearLayoutManager mLayoutManager;
    private String UID;
    private FirebaseAuth mAuth;
    private boolean isTimeTable;

    private boolean adapterSet;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
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
    @BindView(R.id.days_pager)
    ViewPager daysViewPager;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private DetailActivityPresenter detailActivityPresenter;
    private DetailsAdapter detailsAdapter;
    private SubjectsModel subjectsModel;
    private TimeTableModel timeTableModel;
    private String[] days;
    private int pagerItemValue;
    private int criteria;
    private FirebaseUser user;

    private static final String SUBJECTS_ADDED_PREF = "subPref";
    private DatabaseReference mDatabase;
    private DaysViewPagerAdapter daysViewPagerAdapter;
    private boolean isFirstTime, isFirstTimeAttendanceView;
    private TextView navUsername;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        days = getNames(Days.class);

        daysViewPagerAdapter = new DaysViewPagerAdapter(this);
        daysViewPagerAdapter.assignNew(true);
        daysViewPager.setAdapter(daysViewPagerAdapter);

        daysViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                pagerItemValue = i;
                try {
                    if (isTimeTable)
                        detailActivityPresenter.fetchTimeTable(timeTableModel, days[i]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    nestedScrollView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        detailActivityPresenter = new DetailActivityPresenter(this);
        detailActivityPresenter.checkLoggedIn(mAuth);

        isFirstTime = detailActivityPresenter.isFirstVisit(this);
        isFirstTimeAttendanceView = detailActivityPresenter.isFirstAttendanceView(this);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new

                LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapterSet = false;

        user = mAuth.getCurrentUser();

        if (user != null) {
            UID = user.getUid();
            if (!checkIfSubjectsAdded())
                startActivity(new Intent(this, MainActivity.class));
        } else {
            startLoginActivity();
            return;
        }

        isTimeTable = true;

        subjectsModel = new SubjectsModel(UID);
        timeTableModel = new TimeTableModel(UID);

        mDatabase = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/subjects");

        AttendanceModel attendanceModel = new AttendanceModel(UID);
        detailActivityPresenter.fetchAttendanceCriteria(attendanceModel);

        //Time table view by default
        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    switch (menuItem.getItemId()) {

                        case R.id.nav_time_table:
                            isTimeTable = true;
                            swipeRefreshLayout.setRefreshing(true);
                            daysViewPagerAdapter.assignNew(true);
                            daysViewPagerAdapter.notifyDataSetChanged();
                            detailActivityPresenter.fetchDayPosition();
                            daysViewPager.setOnTouchListener(null);
                            break;

                        case R.id.nav_logout:
                            new AlertDialog.Builder(this)
                                    .setTitle(R.string.logout)
                                    .setMessage(R.string.logout_message)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> startLoginActivity())
                                    .setNegativeButton(android.R.string.no, null).show();
                            break;

                        case R.id.nav_all_subjects:
                            swipeRefreshLayout.setRefreshing(true);
                            isTimeTable = false;
                            detailActivityPresenter.fetchSubjects(subjectsModel);
                            daysViewPagerAdapter.assignNew(false);
                            daysViewPagerAdapter.notifyDataSetChanged();
                            daysViewPager.setOnTouchListener((arg0, arg1) -> true);
                            break;

                        case R.id.attendance_criteria:
                            finish();
                            Intent intent = new Intent(this, AttendanceActivity.class);
                            intent.putExtra("criteria", criteria);
                            startActivity(intent);
                            break;

                        case R.id.clear_all_subjects:
                            new AlertDialog.Builder(this)
                                    .setTitle(R.string.reset)
                                    .setMessage(R.string.reset_all_data)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                                        DatabaseHelper.clearAlldata(UID);
                                        Intent intent1 = new Intent(DetailActivity.this, MainActivity.class);
                                        startActivity(intent1);
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                            break;

                        case R.id.change_time_table:
                            finish();
                            Intent timeTableIntent = new Intent(this, TimeTableActivity.class);
                            timeTableIntent.putExtra(getString(R.string.is_details), true);
                            startActivity(timeTableIntent);
                            break;

                        case R.id.add_a_subject:
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            View v = getLayoutInflater().inflate(R.layout.add_subject_dialog, null);
                            builder.setView(v);
                            builder.setTitle("Add Subject");
                            builder.setPositiveButton(getString(R.string.add), (dialog, which) -> {
                                EditText editText = v.findViewById(R.id.subject_name);
                                String subjectName = editText.getText().toString();
                                if (subjectName.length() > 0) {
                                    detailActivityPresenter.saveData(subjectName, mDatabase, subjectsModel);
                                    showDaysDialog(subjectName);
                                }
                            });
                            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
                            builder.show();
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

        if (isFirstTime) {
            showSideNavigationPrompt(getToolbarNavigationIcon(toolbar));
        }

        swipeRefreshLayout.setRefreshing(true);

        setNavigationHeader();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            if (!isTimeTable)
                detailActivityPresenter.fetchSubjects(subjectsModel);
            else swipeRefreshLayout.setRefreshing(false);
        });

        addCustomFont();

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
    }

    public static View getToolbarNavigationIcon(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = TextUtils.isEmpty(toolbar.getNavigationContentDescription());
        CharSequence contentDescription = !hadContentDescription ? toolbar.getNavigationContentDescription() : "navigationIcon";
        toolbar.setNavigationContentDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setNavigationContentDescription ensures its existence
        View navIcon = null;
        if (potentialViews.size() > 0) {
            navIcon = potentialViews.get(0); //navigation icon is ImageButton
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setNavigationContentDescription(null);
        return navIcon;
    }

    private void addCustomFont() {
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    detailActivityPresenter.applyFontToMenuItem(subMenuItem, this);
                }
            }

            detailActivityPresenter.applyFontToMenuItem(mi, this);
        }
    }

    private void showDaysDialog(String subName) {
        ArrayList mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final DatabaseReference[] mDatabaseTimeTable = new DatabaseReference[1];
        // Set the dialog title
        builder.setTitle(R.string.add_to_time_table)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(days, null,
                        (dialog, which, isChecked) -> {
                            if (isChecked) {
                                mDatabaseTimeTable[0] = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/table/" + days[which]);
                                detailActivityPresenter.saveData(subName, mDatabaseTimeTable[0], subjectsModel);
                                // If the user checked the item, add it to the selected items
                                if (isTimeTable)
                                    detailActivityPresenter.fetchTimeTable(timeTableModel, days[pagerItemValue]);
                                else detailActivityPresenter.fetchSubjects(subjectsModel);


                            } else {
                                // Else, if the item is already in the array, remove it
                                mDatabaseTimeTable[0] = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/table/" + days[which]);
                                detailActivityPresenter.removeData(subName, mDatabaseTimeTable[0], subjectsModel);
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    // Users clicked OK, so save the mSelectedItems results somewhere
                    // or return them to the component that opened the dialog
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                });

        builder.show();

    }

    private boolean checkIfSubjectsAdded() {
        SharedPreferences prefs = getSharedPreferences(SUBJECTS_ADDED_PREF, MODE_PRIVATE);
        boolean added = prefs.getBoolean("subAdded", true);
        return added;
    }

    private void setAttendanceView() {

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            FrameLayout card = (FrameLayout) mLayoutManager.findViewByPosition(0);
            if (card != null) {
                DetailsAdapter.MyViewHolder viewHolder = (DetailsAdapter.MyViewHolder) mRecyclerView.getChildViewHolder(card);
                showFabPromptFor(viewHolder.percentageTextView);
            }
            //Do something after 100ms
        }, 200);

    }

    public void showFabPromptFor(View view) {
        Typeface typeface = ResourcesCompat.getFont(this, R.font.product_sans);
        new MaterialTapTargetPrompt.Builder(this)
                .setTarget(view)
                .setPrimaryText("Add Previous Attendance")
                .setSecondaryText("Tap to add previous attendance details")
                .setPrimaryTextTypeface(typeface)
                .setSecondaryTextTypeface(typeface)
                .setPromptStateChangeListener((prompt, state) -> {
                    if (!(state == MaterialTapTargetPrompt.STATE_REVEALED)) {
                        isFirstTimeAttendanceView = false;
                        detailActivityPresenter.toggleFirstVisitAttendance(DetailActivity.this, false);
                    }
                })
                .show();
    }

    private void setNavigationHeader() {

        View headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.name_text_view);
        TextView navEmail = headerView.findViewById(R.id.email_text_view);
        CircleImageView imageView = headerView.findViewById(R.id.profile_pic);
        navEmail.setText(user.getEmail());
        String displayName = user.getDisplayName();
        if (displayName == null || displayName.length() == 0) {
            fetchDisplayName();
        } else navUsername.setText(displayName);
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imageView);
    }

    private void fetchDisplayName() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/name");
        detailActivityPresenter.fetchName(reference, subjectsModel);
    }

    @Override
    public void onNameFetched(String name) {
        navUsername.setText(name);
    }

    public void showSideNavigationPrompt(View view) {
        Typeface typeface = ResourcesCompat.getFont(this, R.font.product_sans);
        final MaterialTapTargetPrompt.Builder tapTargetPromptBuilder = new MaterialTapTargetPrompt.Builder(this)
                .setPrimaryText("Navigation Drawer")
                .setSecondaryText(R.string.check_other_options)
                .setPrimaryTextTypeface(typeface)
                .setSecondaryTextTypeface(typeface)
                .setFocalPadding(R.dimen.dp40)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setMaxTextWidth(R.dimen.tap_target_menu_max_width)
                .setIcon(R.drawable.ic_menu)
                .setCaptureTouchEventOutsidePrompt(true);

        tapTargetPromptBuilder.setTarget(view);

        tapTargetPromptBuilder.setPromptStateChangeListener((prompt, state) -> {
            if (!(state == MaterialTapTargetPrompt.STATE_REVEALED || state == MaterialTapTargetPrompt.STATE_REVEALING)) {
                isFirstTime = false;
                detailActivityPresenter.toggleFirstVisit(DetailActivity.this, false);
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (nestedScrollView.getVisibility() != View.VISIBLE)
                        setAttendanceView();
                    //Do something after 100ms
                }, 200);
            }
        });
        tapTargetPromptBuilder.show();
    }

    public String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (!isFirstTime)
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

    @Override
    protected void onStop() {
        super.onStop();
        adapterSet = false;
    }

    @Override
    public void onDayPositionFetched(int position) {
        pagerItemValue = position;
        daysViewPager.setCurrentItem(position);
        if (!isTimeTable)
            detailActivityPresenter.fetchSubjects(subjectsModel);
        else {
            detailActivityPresenter.fetchTimeTable(timeTableModel, days[position]);
        }
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
        nestedScrollView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        if (isFirstTimeAttendanceView && !isFirstTime)
            setAttendanceView();
    }

    @Override
    public void startMainActivity() {
        swipeRefreshLayout.setRefreshing(false);
        if (!isTimeTable) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_sub_added)
                    .setMessage(R.string.promt_add_subject)
                    .setIcon(android.R.drawable.ic_input_add)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        DatabaseHelper.clearAlldata(UID);
                        Intent intent1 = new Intent(DetailActivity.this, MainActivity.class);
                        startActivity(intent1);
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            nestedScrollView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttendanceFetched(int criteria) {
        this.criteria = criteria;
        detailsAdapter = new DetailsAdapter(this, criteria);
        detailActivityPresenter.fetchDayPosition();
    }

    private void startLoginActivity() {
        finish();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        return;
    }

    @Override
    public void onItemClick(String key) {
        DialogFragment newFragment = new AttendanceDialogFragment();
        ((AttendanceDialogFragment) newFragment).setArguments(key);
        newFragment.show(getSupportFragmentManager(), "attendance");
    }

    @Override
    public void onAttendanceMarked(HashMap<String, Object> result) {
        updateAttendance(result);
    }

    @Override
    public void onLongTapped(String key) {
        Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogPositiveClick(HashMap<String, Object> result) {
        if (result != null)
            updateAttendance(result);
    }

    private void updateAttendance(HashMap<String, Object> result) {
        detailActivityPresenter.updateAttendance(result, subjectsModel);
        if (!isTimeTable)
            detailActivityPresenter.fetchSubjects(subjectsModel);
        else detailActivityPresenter.fetchTimeTable(timeTableModel, days[pagerItemValue]);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DetailsAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = (String) ((DetailsAdapter.MyViewHolder) viewHolder).subject_textView.getText();
            // remove the item from recycler view
            int itemPosition = viewHolder.getAdapterPosition();
            detailsAdapter.removeItem(itemPosition);
            Snackbar snackbar;
            AtomicBoolean undoSelected = new AtomicBoolean(false);
            // showing snack bar with Undo option
            if (isTimeTable) {
                snackbar = Snackbar
                        .make(coordinatorLayout, name + " removed from Time Table!", Snackbar.LENGTH_LONG);
            } else {
                snackbar = Snackbar
                        .make(coordinatorLayout, name + " removed from Subjects list!", Snackbar.LENGTH_LONG);
            }

            snackbar.setAction(R.string.undo, view -> {
                undoSelected.set(true);
                detailsAdapter.restoreItem(name, itemPosition);
            });

            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if(!undoSelected.get()){
                        if(!isTimeTable) {
                            detailActivityPresenter.removeData(name, mDatabase, subjectsModel);
                            for(int i = 0; i<7; i++) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/table/" + days[i]);
                                detailActivityPresenter.removeData(name, databaseReference, subjectsModel);
                            }
                        }
                        else {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/table/" + days[pagerItemValue]);
                            detailActivityPresenter.removeData(name, databaseReference, subjectsModel);
                        }
                    }

                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
