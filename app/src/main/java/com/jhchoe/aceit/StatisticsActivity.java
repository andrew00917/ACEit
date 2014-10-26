package com.jhchoe.aceit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class StatisticsActivity extends Activity {

    private ACEitApplication application;

    private String account;
    private int level;

    private final float hundred = (float) 100;
    private float t[];
    private float r[];
    private float s[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        application = (ACEitApplication) getApplication();
        account = application.getCurrentAccount();


        try {
            Intent intent = this.getIntent();
            level = intent.getIntExtra("level_pressed", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String txt;
        if (level == 0) {
            txt = "Error: loading level pressed. Try again.";
        }
        else {
            t = application.getTotalCount();
            r = application.getRightCount();
            s = application.getSkippedCount();
            txt = "Account: " + account + "\n"
                    + "Number of total words: " + (int) t[level-1] + "\n"
                    + "Number of correct words: " + (int) r[level-1] + "\n"
                    + "Number of skipped words: " + (int) s[level-1] + "\n"
                    + "Number of words played: " + (int) (t[level-1]-s[level-1]) + "\n"
                    + "Percent correct: " + ((r[level-1]/t[level-1])*hundred) + "%\n"
                    + "Percent skipped: " + ((s[level-1]/t[level-1])*hundred) + "%\n"
                    + "Percent correct out of played: " + ((r[level-1]/(t[level-1]-s[level-1]))*hundred) + "%";
        }

        setText(txt);
    }

    /**
     * Set the text in the dialog statistics activity
     */
    private void setText(String txt) {
        TextView textV = (TextView) findViewById(R.id.stat);
        textV.setText(txt);
    }

    /**
     * Statistics activity on click method
     *
     * @param view
     */
    public void done(View view) {
        this.finish();
    }
}