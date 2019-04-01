package com.yanshao.yanimageload.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * 图片圆形
 * @author WANGYAN
 * 微博：@Wang丶Yan
 * Github:https://github.com/yanshao
 * 创建时间：2019-03-01
 */
public class CircleImageDrawable extends Drawable {
    private Paint mPaint;
    private int mWidth;
    private Bitmap mBitmap;

    public CircleImageDrawable(Bitmap bitmap) {
        if (bitmap.getWidth() > bitmap.getHeight()) {
            mBitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - bitmap.getHeight() / 2, 0, bitmap.getHeight(), bitmap.getHeight());
        } else if (bitmap.getWidth() < bitmap.getHeight()) {
            mBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth());
        } else {
            mBitmap = bitmap;
        }
        BitmapShader bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
        mWidth = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
    }


    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mWidth / 2 , mWidth / 2, mPaint);
       // canvas.drawCircle(mWidth / 2, mWidth / 2 , mWidth / 2-20, mPaint);
     /*   mPaint.setStyle(Paint.Style.STROKE);

        mPaint.setStrokeWidth(10);
        canvas.drawCircle(mWidth / 2, mWidth / 2,mWidth / 2-10,mPaint);*/

    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mWidth;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
