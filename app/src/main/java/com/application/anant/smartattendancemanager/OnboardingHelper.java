package com.application.anant.smartattendancemanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.FullscreenPromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

import static android.content.Context.MODE_PRIVATE;

public class OnboardingHelper {

    private static final String IS_FIRST_TIME = "isFirstTime";
    private Activity activity;

    public OnboardingHelper(Activity activity) {
        this.activity = activity;
    }

    public void showFullscreenRectPrompt(String title, String message, View view) {
        Typeface typeface = ResourcesCompat.getFont(activity, R.font.product_sans);
        SpannableString mNewTitle = new SpannableString(title);
        mNewTitle.setSpan(new NavigationTypeFace("", typeface), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        SpannableString mNewMessage = new SpannableString(message);
        mNewTitle.setSpan(new NavigationTypeFace("", typeface), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        new MaterialTapTargetPrompt.Builder(activity)
                .setTarget(view)
                .setPrimaryText(mNewTitle)
                .setSecondaryText(mNewMessage)
                .setPromptBackground(new FullscreenPromptBackground())
                .setPromptFocal(new RectanglePromptFocal())
                .show();
    }

    public void toggleFirstTime(boolean added) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(IS_FIRST_TIME, MODE_PRIVATE).edit();
        editor.putBoolean("isFirstTime", added);
        editor.apply();
    }

    public boolean checkIfFirstVisit() {
        SharedPreferences prefs = activity.getSharedPreferences(IS_FIRST_TIME, MODE_PRIVATE);
        boolean added = prefs.getBoolean("isFirstTime", true);
        return added;
    }

}
