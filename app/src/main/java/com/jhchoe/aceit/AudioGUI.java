package com.jhchoe.aceit;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

public class AudioGUI {

    protected static final int RESULT_SPEECH = 1;

    private Activity activity;
    private Context context;

    /**
     * Constructor
     */
    public AudioGUI(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }



    public void destroy() {

    }
}