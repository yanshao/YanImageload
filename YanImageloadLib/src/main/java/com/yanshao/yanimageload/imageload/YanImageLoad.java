package com.yanshao.yanimageload.imageload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.yanshao.yanimageload.util.CircleImageDrawable;
import com.yanshao.yanimageload.util.FileUtils;
import com.yanshao.yanimageload.util.RoundImageDrawable;
import com.yanshao.yanimageload.util.Scheme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class YanImageLoad {
    private static Context mContext;
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private static YanImageLoad instance;

    private YanImageLoad(Context context) {
        mContext=context;
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils(mContext);
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);

    }

    public static YanImageLoad getInstance(Context context) {

        if (instance == null)
        {
            synchronized (YanImageLoad.class)
            {
                if (instance == null)
                {
                    instance = new YanImageLoad(context);
                }
            }
        }
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
       //
        switch (Scheme.ofUri(url))
        {
            case HTTP:
                netBitmap(ivPic, url, tag);

                break;
            case PATH:
                SDBitmap(ivPic, url, tag);

                break;
            case FILE:
                Log.e("yy","eeeeeeeeee");

                break;
            case CONTENT:
                break;
            case ASSETS:
                break;
            case DRAWABLE:
                break;
            case UNKNOWN:
            default:

        }

    }

    /**
     * 网络图片 加载
     * @param ivPic
     * @param url
     * @param tag
     */
    private  void netBitmap(ImageView ivPic,String url,int tag){
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

            Log.e("yyyy","============内存===");
            return ;
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
            Log.e("yyyy","============本地===");
            return;
        }
        //网络缓存
        mNetCacheUtils.getBitmapFromNet(ivPic, url, tag );
    }

    /**
     *  本地
     * @param
     */
   private void SDBitmap(ImageView ivPic,String url,int tag){
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

           Log.e("yyyy","=======本地图片=====内存缓存===");
           return ;
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
           Log.e("yyyy","=====本地图片=======本地缓存===");
           return;
       }
       File file=new File(url);
       try {
           bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

           if (bitmap != null) {
               bitmap=FileUtils.compress(bitmap,ivPic);
               if (tag == 1) {
                   ivPic.setImageDrawable(new RoundImageDrawable(bitmap));
               } else if (tag == 2) {
                   ivPic.setImageDrawable(new CircleImageDrawable(bitmap));
               } else {
                   ivPic.setImageBitmap(bitmap);
               }
               //从本地获取图片后,保存至内存中
               mMemoryCacheUtils.setBitmapToMemory(url, bitmap);
               mLocalCacheUtils.setBitmapToLocal(url, bitmap);
               Log.e("yyyy","=====本地图片=======sd卡===");
               return;
           }
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }

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


