package com.yanshao.yanimageload.imageload;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import com.yanshao.yanimageload.util.CircleImageDrawable;
import com.yanshao.yanimageload.util.RoundImageDrawable;


public class YanImageLoad {
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private static YanImageLoad instance = new YanImageLoad();

    private YanImageLoad() {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);

    }

    public static YanImageLoad getInstance( ) {

        return instance;
    }

    public Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if (bitmap != null) {
            mMemoryCacheUtils.setBitmapToMemory(url, bitmap);
            return bitmap;
        }

        return bitmap;
    }

    /**
     *
     * @param ivPic  imageview
     * @param url  图片url
     * @param tag  1 圆角矩形 2 圆形  other  正常
     */
    public void disPlay(final ImageView ivPic, String url, int tag) {

        Bitmap bitmap;
        //内存缓存
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap != null) {
            if (tag == 1) {
                ivPic.setImageDrawable(new RoundImageDrawable(bitmap));
            } else if (tag == 2) {
                ivPic.setImageDrawable(new CircleImageDrawable(bitmap));
            } else {
                ivPic.setImageBitmap(bitmap);
            }

            return;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if (bitmap != null) {

            if (tag == 1) {
                ivPic.setImageDrawable(new RoundImageDrawable(bitmap));
            } else if (tag == 2) {
                ivPic.setImageDrawable(new CircleImageDrawable(bitmap));
            } else {
                ivPic.setImageBitmap(bitmap);
            }
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtils.setBitmapToMemory(url, bitmap);

            return;
        }
        //网络缓存
        mNetCacheUtils.getBitmapFromNet(ivPic, url, tag );
    }


    @SuppressLint("NewApi")
    public void disPlay(View ivPic, String url) {
        Bitmap bitmap;
        //内存缓存
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap != null) {
            //ivPic.setImageBitmap(bitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                //Android系统大于等于API16，使用setBackground
                ivPic.setBackground(new BitmapDrawable(bitmap));
            } else {
                //Android系统小于API16，使用setBackground
                ivPic.setBackgroundDrawable(new BitmapDrawable(bitmap));

            }

            return;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if (bitmap != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                //Android系统大于等于API16，使用setBackground
                ivPic.setBackground(new BitmapDrawable(bitmap));
            } else {
                //Android系统小于API16，使用setBackground
                ivPic.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtils.setBitmapToMemory(url, bitmap);
            return;
        }
        //网络缓存
        mNetCacheUtils.getBitmapFromNet(ivPic, url );
    }


}


