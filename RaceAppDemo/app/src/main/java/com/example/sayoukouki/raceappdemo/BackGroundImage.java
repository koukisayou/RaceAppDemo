package com.example.sayoukouki.raceappdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Canvasクラスを用いて背景画像を描画する
 * Created by SayouKouki on 2017/07/18.
 */

public class BackGroundImage extends View {
    /**
     * Field
     */
    private Paint paint;
    private Bitmap bmp = null;

    /**
     * Constructor
     * @param context
     * @param attrs
     */
    public BackGroundImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.garage_sample);
    }

    /**
     * 描画
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // 背景、半透明
        canvas.drawColor(Color.argb(125, 0, 0, 255));
        // Bitmap 画像を表示
        canvas.drawBitmap(bmp, 40, 200, paint);
    }

}
