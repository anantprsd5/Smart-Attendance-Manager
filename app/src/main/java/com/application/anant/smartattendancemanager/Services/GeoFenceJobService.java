package com.application.anant.smartattendancemanager.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.application.anant.smartattendancemanager.Constants;
import com.application.anant.smartattendancemanager.GeoFencing;
import com.application.anant.smartattendancemanager.Helper.JobSchedulerHelper;
import com.application.anant.smartattendancemanager.R;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
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
import java.util.List;
import java.util.Map;

public class GeoFenceJobService extends JobService {

    private final String TAG = GeoFenceJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters job) {
        // To start a recurring Job service which executes itself after every 24 hours starting from 7:00 AM
        if (job.getTag().equals(Constants.JOB_TAG_NON_RECURRING)) {
            JobSchedulerHelper jobSchedulerHelper = new JobSchedulerHelper(this);
            jobSchedulerHelper.scheduleJobRecurring();
        }

        registerGeoFence();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }

    public void registerGeoFence() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String UID = user.getUid();
        final GeoDataClient mGeoDataClient = Places.getGeoDataClient(this);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users/" + UID + "/location");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map != null) {
                    final List<String> placesDataset = new ArrayList<>(map.keySet());
                    if (placesDataset != null && placesDataset.size() > 0) {
                        ref.removeEventListener(this);

                        mGeoDataClient.getPlaceById(map.get(placesDataset.get(0)).toString()).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                                if (task.isSuccessful()) {
                                    PlaceBufferResponse places = task.getResult();
                                    Place myPlace = places.get(0);
                                    GeoFencing geoFencing = new GeoFencing(GeoFenceJobService.this, myPlace);
                                    geoFencing.registerGeofence();
                                } else {
                                    Log.e(TAG, getString(R.string.place_not_found));
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
