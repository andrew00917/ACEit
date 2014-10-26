package com.jhchoe.aceit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.method.Touch;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


public class HelpActivity extends Activity {

    private static String inst = "instruction";
    private int counter;
    private ImageView v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        v = (ImageView) findViewById(R.id.help_inst);
        counter = 1;
    }

    public void click(View view) {
        if (counter < 18) {
            counter++;

            String image = inst + counter;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    getResources().getIdentifier(image, "drawable", getPackageName()));
            v.setImageBitmap(bitmap);
        }
        else {
            finish();
        }
    }

    /**
     *
     * @param bm        bitmap to resize
     * @param newHeight new height for bitmap
     * @param newWidth  new width for bitmap
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
