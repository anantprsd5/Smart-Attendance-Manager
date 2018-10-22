package com.example.anant.smartattendancemanager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.anant.smartattendancemanager.Reciever.GeoFenceBroadcastReciever;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class GeoFencing {

    private static final long GEOFENCE_TIMEOUT = 39600000;
    private Context mContext;

    private PendingIntent mGeoFencePendingIntent;
    private GeofencingClient mGeofencingClient;
    private Place mPlace;

    public GeoFencing(Context context, Place place) {
        mContext = context;
        mPlace = place;
    }

    public void registerGeofence() {
        mGeofencingClient = LocationServices.getGeofencingClient(mContext);
        try {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("geofenceSetup", "success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("geofenceSetup", "fail");
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void removeGeoFence() {

        if (mGeofencingClient != null) {
            mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Geofences removed
                            // ...
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to remove geofences
                            // ...
                        }
                    });
        }
    }

    public Geofence createGeoFenceObject() {
        Geofence geofence;
        String placeId = mPlace.getId();
        double latitude = mPlace.getLatLng().latitude;
        double longitude = mPlace.getLatLng().longitude;
        float radius = 50.0f;
        geofence = new Geofence.Builder()
                .setRequestId(placeId)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(GEOFENCE_TIMEOUT)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
        return geofence;
    }

    public GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(createGeoFenceObject());
        return builder.build();
    }

    public PendingIntent getGeofencePendingIntent() {
        if (mGeoFencePendingIntent != null) {
            return mGeoFencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeoFenceBroadcastReciever.class);
        mGeoFencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeoFencePendingIntent;
    }
}
