package com.jhchoe.aceit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;

public class LevelActivity extends Activity {

    private ACEitApplication application;

    private String account;
    private boolean unlock[];
    private int unlockedLevel;

    private float t;
    private float r;

    private float total[];
    private float right[];

    private View progressContainer;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        application = (ACEitApplication) getApplication();

        refresh();

        progressContainer = findViewById(R.id.progressContainer);
        progressContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        application.setCurrentAccount(null);
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Initialize || refresh
     */
    private void refresh() {
        setup();

        unlock = new boolean[application.getNumberOfLevels()];
        initialize();
        if (unlockedLevel > 0) {
            unlock = setUnlockList();
        }

        if (application.getCurrentAccount() != null && unlockedLevel >= 0) {
            for (int i = 0; i < application.getNumberOfLevels(); i++) {
                t = total[i];
                r = right[i];

                // Setting the stars for each level
                ImageButton star;
                String temp;
                if ((r / t) > 0.89) {
                    for (int j = 1; j < 4; j++) {
                        temp = "level" + (i + 1) + "_star" + j;
                        star = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                        star.setImageResource(R.drawable.ic_action_important);
                    }
                }
                else if ((r / t) > 0.74) {
                    for (int j = 1; j < 3; j++) {
                        temp = "level" + (i + 1) + "_star" + j;
                        star = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                        star.setImageResource(R.drawable.ic_action_important);
                    }
                    temp = "level" + (i + 1) + "_star" + 3;
                    star = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                    star.setImageResource(R.drawable.ic_action_half_important);
                }
                else if ((r / t) > 0.59) {
                    for (int j = 1; j < 3; j++) {
                        temp = "level" + (i + 1) + "_star" + j;
                        star = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                        star.setImageResource(R.drawable.ic_action_important);
                    }
                }
                else if ((r / t) > 0.44) {
                    temp = "level" + (i + 1) + "_star" + 1;
                    star = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                    star.setImageResource(R.drawable.ic_action_important);

                    temp = "level" + (i + 1) + "_star" + 2;
                    star = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                    star.setImageResource(R.drawable.ic_action_half_important);
                }
                else if ((r / t) > 0.29) {
                    temp = "level" + (i + 1) + "_star" + 1;
                    star = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                    star.setImageResource(R.drawable.ic_action_important);
                }
                else if ((r / t) > 0.14) {
                    temp = "level" + (i + 1) + "_star" + 1;
                    star = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                    star.setImageResource(R.drawable.ic_action_half_important);
                }
                else {
                    // Set star buttons disabled for each level
                    if (i > unlockedLevel) {
                        for (int j = 1; j < 4; j++) {
                            temp = "level" + (i + 1) + "_star" + j;
                            star = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                            star.setVisibility(ImageButton.GONE);
                        }
                    }
                }
            }
        }
        else {
            // Challenge mode
            // Set star buttons disabled for each level
            String temp;
            ImageButton star;
            for (int j = 1; j < 5; j++) {
                for (int k = 1; k < 4; k++) {
                    temp = "level" + j + "_star" + k;
                    star = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                    star.setVisibility(ImageButton.GONE);
                }
            }
        }

        // Set level buttons
        for (int i = 0; i < application.getNumberOfLevels(); i++) {
            if ( ! unlock[i] ) {
                int l = i + 1;
                String temp = "level" + l;
                Button button = (Button) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                button.setVisibility(Button.GONE);

                temp = "level" + l + "_image";
                if (l == 4) {
                    Button b = (Button) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                    b.setVisibility(Button.GONE);
                }
                else if (l == 2 || l == 3) {
                    temp = "level" + l + "_image_1";
                    ImageButton b = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                    b.setVisibility(ImageButton.GONE);
                    temp = "level" + l + "_image_2";
                    b = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                    b.setVisibility(ImageButton.GONE);
                }
                else {
                    ImageButton b = (ImageButton) findViewById(getResources().getIdentifier(temp, "id", getPackageName()));
                    b.setVisibility(ImageButton.GONE);
                }
            }
            else {
                // Set scroll to bottom only to the levels that are unlocked
                ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);
                scroll.post(new Runnable() {
                    @Override
                    public void run() {
                        ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);
                        scroll.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }
    }

    private void setup() {
        account = application.getCurrentAccount();
        application.load();
        unlockedLevel = application.getUnlockedLevel() - 1;
        total = application.getTotalCount();
        right = application.getRightCount();
    }

    /**
     * Initialize if there's no intent information
     */
    private void initialize() {
        unlock[0] = true;
        for (int i = 1; i < application.getNumberOfLevels(); i++) {
            unlock[i] = false;
        }
    }

    /**
     *
     * @return unlock
     */
    private boolean[] setUnlockList() {
        unlock[unlockedLevel] = true;
        for (int i = application.getNumberOfLevels()-1; i >= 0; i--) {
            if (unlock[i]) {
                for (int j = 0; j <= i; j++) {
                    unlock[j] = true;
                }
                return unlock;
            }
        }
        return unlock;
    }

    /**
     * Button method
     *
     * @param view
     */
    public void levelOne(View view) {
        progressContainer.setVisibility(View.VISIBLE);
        application.setPlayIncorrect(false);
        Intent intent = new Intent(this, LevelOneActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Button method
     *
     * @param view
     */
    public void levelTwo(View view) {
        progressContainer.setVisibility(View.VISIBLE);
        application.setPlayIncorrect(false);
        Intent intent = new Intent(this, LevelTwoActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Button method
     *
     * @param view
     */
    public void levelTwoDraw(View view) {
        progressContainer.setVisibility(View.VISIBLE);
        application.setPlayIncorrect(false);
        Intent intent = new Intent(this, LevelTwoDrawActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Button method
     *
     * @param view
     */
    public void levelThree(View view) {
        progressContainer.setVisibility(View.VISIBLE);
        application.setPlayIncorrect(false);
        Intent intent = new Intent(this, LevelThreeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Button method
     *
     * @param view
     */
    public void levelThreeDraw(View view) {
        progressContainer.setVisibility(View.VISIBLE);
        application.setPlayIncorrect(false);
        Intent intent = new Intent(this, LevelThreeDrawActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Button method
     *
     * @param view
     */
    public void levelFour(View view) {
        progressContainer.setVisibility(View.VISIBLE);
        application.setPlayIncorrect(false);
        Intent intent = new Intent(this, LevelFourActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Statistics button method for each level
     *
     * @param view
     */
    public void statistics(View view) {
        if ( ! account.equals("challenge")) {
            ImageButton b = (ImageButton) view;
            int level_pressed;

            switch(b.getId()) {
                case R.id.level1_star1:
                case R.id.level1_star2:
                case R.id.level1_star3:
                    level_pressed = 1;
                    break;
                case R.id.level2_star1:
                case R.id.level2_star2:
                case R.id.level2_star3:
                    level_pressed = 2;
                    break;
                case R.id.level3_star1:
                case R.id.level3_star2:
                case R.id.level3_star3:
                    level_pressed = 3;
                    break;
                case R.id.level4_star1:
                case R.id.level4_star2:
                case R.id.level4_star3:
                    level_pressed = 4;
                    break;
                default:
                    level_pressed = 1;
                    break;
            }

            Intent intent = new Intent(this, StatisticsActivity.class);
            intent.putExtra("level_pressed", level_pressed);
            startActivity(intent);
        }
    }
}