package com.yanshao.yanimageload.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件管理类
 * @author WANGYAN
 * 微博：@Wang丶Yan
 * Github:https://github.com/yanshao
 * 创建时间：2019-03-20
 */
public class FileUtils {

    public static Bitmap ys(String path, ImageView imageView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

       // options.inSampleSize = caculateInSampleSize(options, Utils.getImageViewSize(imageView).width, Utils.getImageViewSize(imageView).height);

        // 使用获得到的InSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        LogUtils.e("yy", "size==" + bitmap.getByteCount() + "width=" + bitmap.getWidth() + "height=" + bitmap.getHeight());
        //  imageView.setImageBitmap(bitmap);
        return bitmap;


    }


    public static Bitmap compress(Bitmap b, ImageView imageView) {
        Bitmap bitmap = b;
        LogUtils.e("yy", "压缩前图片的大小" + getBitmapSize(b)+ "M宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());

       if (bitmap.getByteCount() > 4096000) {

           bitmap = suofang(bitmap,caculateInSampleSize(bitmap,Utils.getImageViewSize(imageView).width,Utils.getImageViewSize(imageView).height));
        }
        LogUtils.e("yy", "压缩后图片的大小" + getBitmapSize(bitmap) + "M宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());

        return bitmap;
    }


    public static Bitmap suofang(Bitmap bitmap,int rad) {
        Bitmap result = null;
        int radio = rad;
        result = Bitmap.createBitmap(bitmap.getWidth() / radio, bitmap.getHeight() / radio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        RectF rectF = new RectF(0, 0, bitmap.getWidth() / radio, bitmap.getHeight() / radio);
        //将原图画在缩放之后的矩形上
        canvas.drawBitmap(bitmap, null, rectF, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        //  Log.e("yy", "压缩后图片的大小1=" + (result.getByteCount()) + "M宽度为" + result.getWidth() + "高度为" + result.getHeight());
        return result;
    }
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


    public static int caculateInSampleSize(Bitmap bitmap, int reqWidth,
                                           int reqHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);
        }

        return inSampleSize;
    }


public static void saveBitmap(Bitmap bitmap, String picName) {

    File f = new File("/storage/emulated/0/Pictures/", picName);
    if (f.exists()) {
        f.delete();
    }
    try {
        FileOutputStream out = new FileOutputStream(f);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
        LogUtils.i("222", "已经保存");
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}




    /**
     * 从网络获取图片后,保存至本地缓存
     *
     * @param url
     * @param bitmap
     */
    public static void setBitmapToLocal(Context mContext, String url, Bitmap bitmap) {
        try {

            String fileName = MD5Encoder.encode(url);//把图片的url当做文件名,并进行MD5加密
            File file = new File(mContext.getCacheDir(), fileName);

            //通过得到文件的父文件,判断父文件是否存在
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            //把图片保存至本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
