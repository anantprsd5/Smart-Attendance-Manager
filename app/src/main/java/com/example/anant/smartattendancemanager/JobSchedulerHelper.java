package com.example.anant.smartattendancemanager;

import android.content.Context;

import com.example.anant.smartattendancemanager.Services.GeoFenceJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.Calendar;

public class JobSchedulerHelper {

    private Context mContext;
    private FirebaseJobDispatcher dispatcher;

    public JobSchedulerHelper(Context context) {
        mContext = context;
        // Create a new dispatcher using the Google Play driver.
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
    }

    public void scheduleJobNonRecurring() {

        Calendar morning = Calendar.getInstance();
        morning.set(Calendar.HOUR, 7);
        morning.set(Calendar.MINUTE, 0);
        morning.set(Calendar.SECOND, 0);
        morning.set(Calendar.MILLISECOND, 0);
        morning.set(Calendar.AM_PM, Calendar.AM);

        long diff = morning.getTimeInMillis() - System.currentTimeMillis();
        if (diff < 0) {
            morning.add(Calendar.DAY_OF_MONTH, 1);
            diff = morning.getTimeInMillis() - System.currentTimeMillis();
        }
        int startSeconds = (int) (diff / 1000); // tell the start seconds
        int endSencods = startSeconds + 60; // within Five minutes

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(GeoFenceJobService.class)
                // uniquely identifies the job
                .setTag(Constants.JOB_TAG_NON_RECURRING)
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(startSeconds, endSencods))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_ANY_NETWORK
                )
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public void cancelJob() {
        dispatcher.cancelAll();
    }

    public void scheduleJobRecurring() {

        int hours = 24 * 60 * 60;  //To run a job with an interval of 24 hours

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(GeoFenceJobService.class)
                // uniquely identifies the job
                .setTag(Constants.JOB_TAG_RECURRING)
                // one-off job
                .setRecurring(true)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(hours, hours + 60))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_ANY_NETWORK
                )
                .build();

        dispatcher.mustSchedule(myJob);
    }
}
