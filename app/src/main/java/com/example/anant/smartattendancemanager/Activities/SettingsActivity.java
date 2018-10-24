package com.example.anant.smartattendancemanager.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.anant.smartattendancemanager.AlarmHelper;
import com.example.anant.smartattendancemanager.JobSchedulerHelper;
import com.example.anant.smartattendancemanager.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.anant.smartattendancemanager.Activities.SettingsActivity.MainPreferenceFragment.addressPreference;
import static com.example.anant.smartattendancemanager.Activities.SettingsActivity.MainPreferenceFragment.timePreference;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 100;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;
    private static PlacePicker.IntentBuilder builder;
    private static Context mContext;
    private static Activity activity;
    public static TimePickerDialog.OnTimeSetListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        builder = new PlacePicker.IntentBuilder();
        mContext = this;
        activity = this;
        listener = this;

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        String time = String.format("%02d:%02d", i, i1);
        timePreference.setSummary(time);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("time", time);
        editor.apply();
        AlarmHelper alarmHelper = new AlarmHelper(this, i, i1);
        alarmHelper.setAlarm();
    }

    private static void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        new TimePickerDialog(activity, listener, hour, minute,
                DateFormat.is24HourFormat(activity)).show();
    }

    public static class MainPreferenceFragment extends PreferenceFragment
            implements TimePicker.OnTimeChangedListener {
        public static Preference addressPreference;
        private DatabaseReference ref;
        String UID;
        private DatabaseReference mDatabase;
        // The entry points to the Places API.
        private GeoDataClient mGeoDataClient;
        public static Preference timePreference;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UID = user.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            ref = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/location");
            mGeoDataClient = Places.getGeoDataClient(getActivity());

            addressPreference = findPreference(getString(R.string.college_address));
            addressPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map != null) {
                        final List<String> placesDataset = new ArrayList<>(map.keySet());

                        Log.d("checkID", map.get(placesDataset.get(0)).toString());
                        ref.removeEventListener(this);

                        mGeoDataClient.getPlaceById(map.get(placesDataset.get(0)).toString()).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                                if (task.isSuccessful()) {
                                    PlaceBufferResponse places = task.getResult();
                                    Place myPlace = places.get(0);
                                    addressPreference.setSummary(myPlace.getName());
                                    places.release();
                                } else {
                                    Log.e(TAG, "Place not found.");
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Preference preference = findPreference(getString(R.string.enable_geofencing));

            if (PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getBoolean(preference.getKey(), false)) {
                addressPreference.setEnabled(true);
                preference.setSummary(R.string.geofence_enabled);
            } else {
                preference.setSummary(R.string.geofence_disabled);
                addressPreference.setEnabled(true);
            }

            bindPreferenceSummaryToValue(preference);

            timePreference = findPreference(getString(R.string.college_time));
            timePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showTimePicker();
                    return false;
                }
            });
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String time = preferences.getString("time", "No time set");
            timePreference.setSummary(time);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                if (resultCode == RESULT_OK) {
                    Place selectedPlace = PlacePicker.getPlace(getActivity(), data);
                    savePlaceId(selectedPlace.getId());
                    addressPreference.setSummary(selectedPlace.getName());
                    scheduleEnablingGeofence(true);
                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

        private void savePlaceId(String id) {
            HashMap<String, String> places = new HashMap();
            places.put("placeId", id);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/users/" + UID + "/location", places);
            mDatabase.updateChildren(childUpdates);
        }

        @Override
        public void onTimeChanged(TimePicker timePicker, int i, int i1) {

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            if (preference instanceof SwitchPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.

                boolean isLocationEnabled = (Boolean) value;
                SwitchPreference listPreference = (SwitchPreference) preference;
                scheduleEnablingGeofence(isLocationEnabled);
                if (isLocationEnabled) {
                    listPreference.setSummary(R.string.geofence_enabled);
                } else listPreference.setSummary(R.string.geofence_disabled);
            }
            return true;
        }
    };

    private static void scheduleEnablingGeofence(boolean isEnabled) {
        JobSchedulerHelper helper = new JobSchedulerHelper(mContext);
        if (isEnabled) {
            addressPreference.setEnabled(true);
            requestLocationPermission();
        } else {
            addressPreference.setEnabled(false);
            helper.cancelJob();
        }
    }

    private static void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            scheduleJob();
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scheduleJob();
                } else {
                    Toast.makeText(this, R.string.location_access_required,
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private static void scheduleJob() {
        JobSchedulerHelper helper = new JobSchedulerHelper(mContext);
        helper.scheduleJobNonRecurring();
    }
}