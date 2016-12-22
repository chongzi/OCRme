package com.ashomok.imagetotext.ocr_task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.ashomok.imagetotext.R;

/**
 * Created by Iuliia on 24.12.2015.
 */
public final class OCRAnimationView extends View implements BitmapTaskDelegate {
    private float dX;

    private Bitmap scanband;
    private int screenW;
    private int screenH;
    private float X;
    private float Y;
    private static final int scanBandW = 50;

    private static final float a = 77f;
    private static final float b = 151f;
    private static final float c = 28f;
    private static final float t = 120 * -256f;

    private Bitmap background;


    public OCRAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        X = 0;
        Y = 0;
        dX = 0.7f; //horizontal speed

        scanband = BitmapFactory.decodeResource(getResources(), R.drawable.scan_band); //load a scanband image
    }

    public void setImageUri(String imageUri) {
        BitmapAsyncTask bitmapAsyncTask = new BitmapAsyncTask(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            bitmapAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageUri);
        } else {
            bitmapAsyncTask.execute(imageUri);
        }
    }


    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenW = w;
        screenH = h;

        //TODO forbid rotation

        if (background != null) {
            background = Bitmap.createScaledBitmap(background, w, h, true); //Resize background to fit the screen.
        }

        scanband = Bitmap.createScaledBitmap(scanband, scanband.getWidth(), h, true);
        if (X > screenW - scanBandW) {
            X = screenW - scanBandW;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (background != null) {
            //Draw background
            canvas.drawBitmap(background, 0, 0, null);

            //todo   Avoid object allocations during draw/layout operations (preallocate and reuse instead)
            Bitmap croppedBitmap = Bitmap.createBitmap(background, (int) X, 0, scanBandW, screenH);
            Paint paint = new Paint();

            ColorMatrix cm = new ColorMatrix();

            cm.set(new float[]{a, b, c, 0, t, a, b, c, 0, t, a, b, c, 0, t, 0, 0, 0, 1, 0});
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(croppedBitmap, X, 0, paint);
        }

        //Compute scanband speed and location.
        X += dX; //Increase or decrease horizontal position.
        if ((X > (screenW - scanBandW)) || (X < 0)) {
            dX = (-1f) * dX; //Reverse speed when one side hit.
        }

        //Draw scanband
        if (dX > 0) //moving from left to right
        {
            canvas.drawBitmap(scanband, X + scanBandW, Y, null);
        } else {
            canvas.drawBitmap(scanband, X, Y, null);
        }

        //Call the next frame.
        invalidate();
    }

    @Override
    public void TaskCompletionResult(Bitmap result) {
        background = result;

        if (background != null) {
            background = Bitmap.createScaledBitmap(background, screenW, screenH, true); //Resize background to fit the screen.
        }
    }
}
