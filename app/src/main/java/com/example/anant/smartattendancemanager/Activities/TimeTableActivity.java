package com.example.anant.smartattendancemanager.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.anant.smartattendancemanager.Days;
import com.example.anant.smartattendancemanager.Fragments.SubjectsFragment;
import com.example.anant.smartattendancemanager.R;

import java.util.Arrays;


public class TimeTableActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private boolean isDetails;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.time_table);
        setSupportActionBar(toolbar);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //Toolbar is now an action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isDetails = getIntent().getBooleanExtra(getString(R.string.is_details), false);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(7);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(view1 -> {
            if (!isDetails) {
                Intent intent = new Intent(this, AttendanceActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, DetailActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isDetails) {
            Intent intent = new Intent(this, DetailActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 7;
        private String tabTitles[] = getNames(Days.class);

        public String[] getNames(Class<? extends Enum<?>> e) {
            return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
        }

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return SubjectsFragment.newInstance(position + 1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}
