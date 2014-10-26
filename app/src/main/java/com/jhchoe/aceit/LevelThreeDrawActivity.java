package com.jhchoe.aceit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class LevelThreeDrawActivity extends Activity implements TextToSpeech.OnInitListener {

    private ACEitApplication application;

    private TextToSpeech tts;
    private ArrayList<String> wordList;
    private ArrayList<Boolean> correct;
    private boolean unused[];
    private String currentText;

    private int skipped;
    private int counter;

    private View progressContainer;
    private ImageView imageV;
    private ImageView gestureImageV;

    private GestureOverlayView gesture;
    private Canvas canvas;

    private TextView counterV;
    //	private Spinner colorSpinner;

    private Bitmap bm;
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_three_draw);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        application = (ACEitApplication) getApplication();
        progressContainer = findViewById(R.id.progressContainer);
        progressContainer.setVisibility(View.INVISIBLE);

        tts = new TextToSpeech(this, this);
        tts.setSpeechRate((float) 0.75);
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
        imageV = (ImageView) findViewById(R.id.imageV);
        gestureImageV = (ImageView) findViewById(R.id.gestureCanvasV);
        gesture = (GestureOverlayView) findViewById(R.id.gestures);
        Spinner sizeSpinner = (Spinner) findViewById(R.id.size_choice_three);
//		colorSpinner = (Spinner) findViewById(R.id.color_choice_three);
        counterV = (TextView) findViewById(R.id.counter_level3);

        width = gestureImageV.getWidth();
        height = gestureImageV.getHeight();

        wordList = application.getWordList();

        unused = new boolean[wordList.size()];
        correct = new ArrayList<Boolean>();
        for (int i = 0; i < wordList.size(); i++) {
            if (i < 10) {
                correct.add(false);
            }
            unused[i] = true;
        }

        // Set spinner on item selected listener
        sizeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // Draw on canvas what is already on the gesture
                Bitmap bit = toBitmap(gesture, gesture.getGestureColor(), gesture.getGestureStrokeWidth());
                gesture.cancelClearAnimation();
                gesture.clear(true);
                canvas.drawBitmap(bit, 0f, 0f, null);
                // attach the updated canvas to image view.
                gestureImageV.setImageDrawable(new BitmapDrawable(getResources(), bm));
                if (gestureImageV.getVisibility() == ImageView.INVISIBLE) {
                    gestureImageV.setVisibility(ImageView.VISIBLE);
                }
                // then change the stroke width for the gesture view.
                String temp = arg0.getItemAtPosition(arg2).toString();
                if (temp.equals("S")) {
                    gesture.setGestureStrokeWidth(10f);
                }
                else if (temp.equals("M")) {
                    gesture.setGestureStrokeWidth(30f);
                }
                else if (temp.equals("L")) {
                    gesture.setGestureStrokeWidth(50f);
                }
                else if (temp.equals("XL")) {
                    gesture.setGestureStrokeWidth(70f);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

//		colorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				// Draw on canvas what is already on the gesture
//				Bitmap bit = toBitmap(gesture, gesture.getGestureColor(), gesture.getGestureStrokeWidth());
//				gesture.cancelClearAnimation();
//				gesture.clear(true);
//				canvas.drawBitmap(bit, 0f, 0f, null);
//				// attach the updated canvas to image view.
//				gestureImageV.setImageDrawable(new BitmapDrawable(getResources(), bm));
//				
//				// then change the stroke width for the gesture view.
//				switch(arg0.getItemAtPosition(arg2).toString()) {
//				case "Black":
//					gesture.setGestureColor(Color.BLACK);
//					break;
//				case "Red":
//					gesture.setGestureColor(Color.RED);
//					break;
//				case "Yellow":
//					gesture.setGestureColor(Color.YELLOW);
//					break;
//				case "Blue":
//					gesture.setGestureColor(Color.BLUE);
//					break;
//				default:
//					gesture.setGestureColor(Color.BLACK);
//					break;
//				}
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {}
//		});

        skipped = 0;
        counter = 0;

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
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        float[] score = {counter, this.getNumberCorrect(), skipped};
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    /**
     * Run
     */
    private void run() {
        gesture.cancelClearAnimation();
        gesture.clear(true);
        gestureImageV.setVisibility(ImageView.INVISIBLE);

        if ( ! correct.get(counter)) {
            int rand = Math.round(getUnusedRandomNumber(wordList.size()));

            currentText = wordList.get(rand);

            TextView textV = (TextView) findViewById(R.id.level3_text);
            if (currentText.equals("santa_claus")) {
                textV.setText("Santa\nClaus");
            }
            else {
                textV.setText(currentText);
            }

            String image = currentText;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    getResources().getIdentifier(image, "drawable", getPackageName()));
            Bitmap scaledBm = getResizedBitmap(bitmap, imageV.getHeight(), imageV.getWidth());
            imageV.setImageBitmap(scaledBm);

            bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bm);
        }
        else {
            counter++;
            run();
        }

        counterV.setText(getCount());
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
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
            Log.e("TTS", "Initilization Failed!");
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
     * Speak button method
     *
     * @param view view
     */
    public void speak(View view) {
        if (currentText.equals("santa_claus")) {
            this.speakOut("Santa Claus");
        }
        else {
            this.speakOut(currentText);
        }
    }

    /**
     * Save button method
     *
     * @param view view
     */
    public void save(View view) {
        // TODO
        view.getRootView().setDrawingCacheEnabled(true);
        Bitmap screenshot = view.getRootView().getDrawingCache();
        view.getRootView().setDrawingCacheEnabled(false);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        screenshot.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(this.getContentResolver(), screenshot, "Screenshot", null);
        //Uri uri = Uri.parse(path);

        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/*");

        // Make sure you put example png image named myImage.png in your
        // directory
        //String imagePath = Environment.getExternalStorageDirectory() + "/myImage.png";

        File imageFileToShare = new File(path);

        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(share, "Share Screenshot!"));
    }

    /**
     * Next button method
     *
     * @param view view
     */
    public void next(View view) {
        if (counter < 9) {
            counter++;
            run();
        }
        else {
            counter++;
            done();
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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LevelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /***********************
     * HELPER METHODS
     ***********************/

    /**
     *
     * @param gesture gesture
     * @param color color
     * @param strokeWidth width of stroke
     * @return temp
     */
    public Bitmap toBitmap(GestureOverlayView gesture, int color, float strokeWidth) {
        boolean BITMAP_RENDERING_ANTIALIAS = true;
        boolean BITMAP_RENDERING_DITHER = true;

        Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas c = new Canvas(temp);


        final Paint paint = new Paint();
        paint.setAntiAlias(BITMAP_RENDERING_ANTIALIAS);
        paint.setDither(BITMAP_RENDERING_DITHER);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);

        Path path = gesture.getGesturePath();
        c.drawPath(path, paint);

        return temp;
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
     * @return -1
     */
    private int getNumberCorrect() {
        return -1;
    }

    /**
     * Helper method
     *
     * @return string
     */
    private String getCount() {
        return counter + "/" + 10;
    }
}