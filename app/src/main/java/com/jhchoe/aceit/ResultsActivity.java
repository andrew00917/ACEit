package com.jhchoe.aceit;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsActivity extends Activity {

    private ACEitApplication application;

    private int level;
    private int unlockedLevel;
    private ArrayList<String> incorrectList;

    private float t;
    private float r;
    private float s;
    private final float hundred = 100;
    private float percent;
    private String text;

    private float total[];
    private float right[];
    private float skip[];

    private Button incorrectB;

    private LayoutTransition mTransitioner;
    private ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setFinishOnTouchOutside(false);

        application = (ACEitApplication) getApplication();

        Intent intent = getIntent();
        float[] score = intent.getFloatArrayExtra("score");

        t = score[0];
        r = score[1];
        s = score[2];

        setText();
        TextView result = (TextView) findViewById(R.id.result);
        result.setVisibility(View.GONE);

        container = new LinearLayout(this);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        resetTransition();

        unlockedLevel = application.getUnlockedLevel();
        incorrectList = application.getIncorrectList();

        if (incorrectList == null || incorrectList.size() == 0 || r == ((float) -1)) {
            // something went wrong with incorrect list or
            // got everything right.
            // disable the replay incorrect button
            incorrectB = (Button) findViewById(R.id.replayIncorrect);
            incorrectB.setVisibility(Button.GONE);
        }

        ImageButton nextButton = (ImageButton) findViewById(R.id.next);
        nextButton.setVisibility(ImageButton.GONE);

        TextView text = (TextView) findViewById(R.id.resultText);
        if ( r == ((float) -1) || application.isPlayingIncorrect()) {
            text.setText("Good work!");

            // Score doesn't count because the user played the draw mode
            // or replay incorrect mode
            starOne(); starTwo(); starThree();
        }
        else {
            level = intent.getIntExtra("level", 1);
            if ( percent >= 30) {
                text.setText("You've won!");
                starOne();
                if (level < 4) {
                    nextLevelEnabled();
                    if (level >= unlockedLevel) {
                        application.setUnlockedLevel(level + 1);
                    }
                }
            }
            if ( percent >= 60) {
                text.setText("Fantastic!");
                starTwo();
            }
            if ( percent >= 90) {
                text.setText("Perfect!");
                starThree();
            }

            // Updating the data on the account
            if (application.getCurrentAccount() != null) {
                total = application.getTotalCount();
                right = application.getRightCount();
                skip = application.getSkippedCount();

                total[level-1] = total[level-1] + t;
                right[level-1] = right[level-1] + r;
                skip[level-1] = skip[level-1] + s;

                application.setTotalCount(total);
                application.setRightCount(right);
                application.setSkippedCount(skip);
            }
            application.save();
        }
    }

    private void nextLevelEnabled() {
        ImageButton b = (ImageButton) findViewById(R.id.next);
        b.setVisibility(ImageButton.VISIBLE);
    }

    /**
     * Change Star one
     */
    private void starOne() {
        ImageView image = (ImageView) findViewById(R.id.star1);
        image.setImageResource(R.drawable.ic_action_important);
    }

    /**
     * Change Star two
     */
    private void starTwo() {
        ImageView image = (ImageView) findViewById(R.id.star2);
        image.setImageResource(R.drawable.ic_action_important);
    }

    /**
     * Change Star three
     */
    private void starThree() {
        ImageView image = (ImageView) findViewById(R.id.star3);
        image.setImageResource(R.drawable.ic_action_important);
    }

    /**
     * Set result text view
     */
    private void setText() {
        TextView result = (TextView) findViewById(R.id.result);
        if ( r == ((float) -1)) {
            // Score doesn't count because the user played the draw mode
            text = "Drawing mode: Good Job!";
        }
        else {
            percent = (r/t) * hundred;
            text = "\n\n     Total: " + (int) t + " | "
                    + "Correct: " + (int) r + "     \n"
                    + "     Incorrect: " + (int) (t - (r + s)) + " | "
                    + "Skipped: " + (int) s + "     \n"
                    + "     Percentage: " + Math.abs(percent) + "%     \n\n";
        }
        result.setText(text);
    }

    /**
     * Toggle up and down animation button for result
     *
     * @param view
     */
    public void toggleResult(View view) {
        long duration;
        mTransitioner.setStagger(LayoutTransition.CHANGE_APPEARING, 30);
        mTransitioner.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 30);
        setupCustomAnimations();
        duration = 500;
        mTransitioner.setDuration(duration);

        ImageButton b = (ImageButton) findViewById(R.id.handle);
        TextView result = (TextView) findViewById(R.id.result);
        if (result.getVisibility() == TextView.GONE) {
            b.setImageResource(R.drawable.ic_action_collapse);
            result.setVisibility(TextView.VISIBLE);
        }
        else {
            b.setImageResource(R.drawable.ic_action_expand);
            result.setVisibility(TextView.GONE);
        }
    }

    /**
     * Go back to level activity
     *
     * @param view
     */
    public void back(View view) {
        application.setPlayIncorrect(false);
        application.setIncorrectList(null);

        Intent intent = new Intent(this, LevelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        application.setPlayIncorrect(false);
        application.setIncorrectList(null);

        Intent intent = new Intent(this, LevelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * Replay button on click method
     *
     * @param view
     */
    public void replay(View view) {
        Intent intent;
        switch(level) {
            case 1:
                intent = new Intent(this, LevelOneActivity.class);
                break;
            case 2:
                intent = new Intent(this, LevelTwoActivity.class);
                break;
            case 3:
                intent = new Intent(this, LevelThreeActivity.class);
                break;
            case 4:
                intent = new Intent(this, LevelFourActivity.class);
                break;
            default:
                intent = new Intent(this, LevelOneActivity.class);
                break;
        }
        if (application.isPlayingIncorrect()) {
            application.setPlayIncorrect(false);
        }
        application.setIncorrectList(null);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Replay incorrect
     *
     * @param view
     */
    public void replayIncorrect(View view) {
        Intent intent;
        switch(level) {
            case 1:
                intent = new Intent(this, LevelOneActivity.class);
                break;
            case 2:
                intent = new Intent(this, LevelTwoActivity.class);
                break;
            case 3:
                intent = new Intent(this, LevelThreeActivity.class);
                break;
            case 4:
                intent = new Intent(this, LevelFourActivity.class);
                break;
            default:
                intent = new Intent(this, LevelOneActivity.class);
                break;
        }
        if ( ! application.isPlayingIncorrect()) {
            application.setPlayIncorrect(true);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Next level button on click method
     *
     * @param view
     */
    public void nextLevel(View view) {
        Intent intent;
        switch(level) {
            case 1:
                intent = new Intent(this, LevelTwoActivity.class);
                break;
            case 2:
                intent = new Intent(this, LevelThreeActivity.class);
                break;
            case 3:
                intent = new Intent(this, LevelFourActivity.class);
                break;
            case 4:
                intent = new Intent(this, LevelActivity.class);
                break;
            default:
                intent = new Intent(this, LevelActivity.class);
                break;
        }
        if (application.isPlayingIncorrect()) {
            application.setPlayIncorrect(false);
        }
        application.setIncorrectList(null);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    /**************************************
     * Animation methods
     **************************************/
    private void resetTransition() {
        mTransitioner = new LayoutTransition();
        container.setLayoutTransition(mTransitioner);
    }

    private void setupCustomAnimations() {
        // Changing while Adding
        PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
        PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
        PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0, 1);
        PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f, 1f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f, 1f);

        final ObjectAnimator changeIn =
                ObjectAnimator.
                        ofPropertyValuesHolder(this, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhScaleX, pvhScaleY).
                        setDuration(mTransitioner.getDuration(LayoutTransition.CHANGE_APPEARING));

        mTransitioner.setAnimator(LayoutTransition.CHANGE_APPEARING, changeIn);

        changeIn.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setScaleX(1f);
                view.setScaleY(1f);
            }
        });

        // Changing while Removing
        Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
        Keyframe kf1 = Keyframe.ofFloat(.9999f, 360f);
        Keyframe kf2 = Keyframe.ofFloat(1f, 0f);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1, kf2);
        final ObjectAnimator changeOut =
                ObjectAnimator.
                        ofPropertyValuesHolder(this, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhRotation).
                        setDuration(mTransitioner.getDuration(LayoutTransition.CHANGE_DISAPPEARING));

        mTransitioner.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, changeOut);

        changeOut.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotation(0f);
            }
        });

        // Adding
        ObjectAnimator animIn = ObjectAnimator.
                ofFloat(null, "rotationY", 90f, 0f).
                setDuration(mTransitioner.getDuration(LayoutTransition.APPEARING));

        mTransitioner.setAnimator(LayoutTransition.APPEARING, animIn);

        animIn.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotationY(0f);
            }
        });

        // Removing
        ObjectAnimator animOut = ObjectAnimator.
                ofFloat(null, "rotationX", 0f, 90f).
                setDuration(mTransitioner.getDuration(LayoutTransition.DISAPPEARING));

        mTransitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);

        animOut.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotationX(0f);
            }
        });
    }
}