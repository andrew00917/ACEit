package com.jhchoe.aceit;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class LevelThreeActivity extends Activity implements TextToSpeech.OnInitListener {

    private ACEitApplication application;

    protected static final int RESULT_SPEECH = 1;

    protected Context context = this;

    protected MotionEvent downStart = null;

    private Handler myHandler = new Handler();
    private TextToSpeech tts;
    private ArrayList<String> wordList;
    private ArrayList<String> incorrectList;
    private ArrayList<String> correct;
    private ArrayList<String> incorrect;
    private boolean unused[];
    private int counter;

    private View progressContainer;
    private TextView textB;
    private ImageButton imageB;
    private TextView counterV;

    private int level;
    private int skipped;
    private String currentText;

    private boolean swipe;
    private float deltaX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_three);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        application = (ACEitApplication) getApplication();
        progressContainer = findViewById(R.id.progressContainer);
        progressContainer.setVisibility(View.INVISIBLE);

        tts = new TextToSpeech(this, this);
        tts.setSpeechRate((float) 0.75);
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    /**
     *
     * @param view view
     */
    public void startGame(View view) {
        progressContainer.setVisibility(View.VISIBLE);

        initialize();

        TextView intr = (TextView) findViewById(R.id.instruction_three);
        intr.setVisibility(TextView.INVISIBLE);

        run();
    }

    /**
     * Initialize
     */
    private void initialize() {
        skipped = 0;
        counter = 0;
        level = 3;

        if (application.isPlayingIncorrect()) {
            incorrectList = application.getIncorrectList();
            unused = new boolean[incorrectList.size()];
            correct = new ArrayList<String>();
            for (int i = 0; i < incorrectList.size(); i++) {
                unused[i] = true;
            }
        }
        else {
            wordList = application.getWordList();
            incorrectList = new ArrayList<String>();
            unused = new boolean[wordList.size()];
            correct = new ArrayList<String>();
            for (int i = 0; i < wordList.size(); i++) {
                unused[i] = true;
            }
        }

        incorrect = new ArrayList<String>();

        counterV = (TextView) findViewById(R.id.counter_three);
        counterV.setText(getCount());

        textB = (TextView) findViewById(R.id.voice_text_three);
        RelativeLayout textV = (RelativeLayout) findViewById(R.id.text_three);
        textV.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        swipe = false;
                        // keep track of the starting down-event
                        downStart = MotionEvent.obtain(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // if moved horizontally more than slop*2, capture the event for ourselves
                        deltaX = event.getX() - downStart.getX();
                        if (Math.abs(deltaX) > ViewConfiguration.get(context).getScaledTouchSlop() * 2) {
                            if (!swipe) {
                                skip();
                                swipe = true;
                            }
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!swipe) {
                            clickAction();
                        }
                        swipe = false;
                        break;

                }
                return true;
            }
        });
        imageB = (ImageButton) findViewById(R.id.voice_image_three);
        imageB.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        swipe = false;
                        // keep track of the starting down-event
                        downStart = MotionEvent.obtain(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // if moved horizontally more than slop*2, capture the event for ourselves
                        deltaX = event.getX() - downStart.getX();
                        if(Math.abs(deltaX) > ViewConfiguration.get(context).getScaledTouchSlop() * 2) {
                            if ( ! swipe) {
                                skip();
                                swipe = true;
                            }
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if ( ! swipe) {
                            clickAction();
                        }
                        swipe = false;
                        break;

                }
                return true;
            }
        });
    }

    /**
     * Run
     */
    private void run() {
        int rand;

        if (application.isPlayingIncorrect()) {
            rand = Math.round(getUnusedRandomNumber(incorrectList.size()));
            currentText = incorrectList.get(rand);
        }
        else {
            rand = Math.round(getUnusedRandomNumber(wordList.size()));
            currentText = wordList.get(rand);
        }

        if (currentText.equals("santa_claus")) {
            textB.setText("Santa\nClaus");
        }
        else {
            textB.setText(currentText);
        }

        String image = currentText;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                getResources().getIdentifier(image, "drawable", getPackageName()));
        Bitmap bm = getResizedBitmap(bitmap, imageB.getHeight(), imageB.getWidth());
        imageB.setImageBitmap(bm);

        counterV.setText(getCount());

        if (progressContainer.getVisibility() == View.VISIBLE) {
            progressContainer.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Done
     */
    private void done() {
        // done with the current game
        // save the score and move on to the next activity
        tts.stop();
        tts.shutdown();

        if (application.isPlayingIncorrect()) {
            application.setPlayIncorrect(false);
        }

        Intent intent = new Intent(this, ResultsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        float[] score = {counter + 1, correct.size(), skipped};
        intent.putExtra("score", score);
        intent.putExtra("level", level);
        application.setIncorrectList(incorrect);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        }
        else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    /**
     * Text to speech method
     *
     * @param text text
     */
    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Activity onClick method
     *
     */
    public void clickAction() {
        myHandler.postDelayed(mMyRunnable, 0);
    }

    private Runnable mMyRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            recordSpeech();
        }
    };

    /**
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String input = text.get(0);

                    input = input.toLowerCase();

                    input = application.validate(input);

                    Toast.makeText(this, "" + input, Toast.LENGTH_LONG).show();

                    if (input.equals(currentText.toLowerCase())) {
                        correct.add(currentText);
                    }
                    else {
                        incorrect.add(currentText);
                    }

                    // next word if counter is still smaller than the word list
                    if (counter >= 9 || (application.isPlayingIncorrect() && counter >= incorrectList.size()-1)) {
                        done();
                    }
                    else {
                        counter++;
                        run();
                    }
                }
                break;
        }
    }

    /**
     * Back to levels activity button method
     *
     * @param view view
     */
    public void backToLevel(View view) {
        Intent intent = new Intent(this, LevelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LevelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Speak hint button method
     * @param view view
     */
    public void voiceHint(View view) {
        if (currentText.equals("santa_claus")) {
            this.speakOut("Santa Claus");
        }
        else {
            this.speakOut(currentText);
        }
    }

    /**
     * Skip button method
     *
     * @param view view
     */
    public void skip(View view) {
        skipped++;
        incorrect.add(currentText);
        if (counter >= 9 || (application.isPlayingIncorrect()
                && counter >= incorrectList.size()-1)) {
            done();
        }
        else {
            counter++;
            run();
        }
    }

    /**
     * Skip method
     */
    private void skip() {
        skipped++;
        incorrect.add(currentText);
        if (counter >= 9 || (application.isPlayingIncorrect()
                && counter >= incorrectList.size()-1)) {
            done();
        }
        else {
            counter++;
            run();
        }
    }

    /**
     * Return unused random number
     *
     * @param n range
     * @return i
     */
    private int getUnusedRandomNumber(int n) {
        Random random = new Random();
        int i = random.nextInt(n);
        while ( ! unused[i]) {
            i = random.nextInt(n);
        }
        unused[i] = false;
        return i;
    }

    /**
     * Helper method
     *
     * @return ret
     */
    private String getCount() {
        String ret;
        if (application.isPlayingIncorrect()) {
            ret = correct.size() + "/" + incorrectList.size();
        }
        else {
            ret = correct.size() + "/" + 10;
        }
        return ret;
    }

    /**
     * Record speech
     *
     */
    public void recordSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast toast = Toast.makeText(context,"Sorry, your device doesn't support Speech Recognition.",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     *
     * @param bm        bitmap
     * @param newHeight new height
     * @param newWidth  new width
     * @return ret
     */
    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int wi = bm.getWidth();
        int he = bm.getHeight();
        float scaleWidth = ((float) newWidth) / wi;
        float scaleHeight = ((float) newHeight) / he;
        float scale = (scaleWidth <= scaleHeight) ? scaleWidth : scaleHeight;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scale, scale);

        // "RECREATE" THE NEW BITMAP
        Bitmap ret = Bitmap.createBitmap(bm, 0, 0, wi, he, matrix, false);
        return ret;
    }
}