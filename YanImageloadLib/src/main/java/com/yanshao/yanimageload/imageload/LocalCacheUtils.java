package com.yanshao.yanimageload.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.yanshao.yanimageload.util.MD5Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LocalCacheUtils {

Context mContext;
    public LocalCacheUtils(Context context) {
        mContext=context;
    }

    /**
     * 从本地读取图片
     * @param url
     */
    public Bitmap getBitmapFromLocal(String url){
        String fileName = null;//把图片的url当做文件名,并进行MD5加密
        try {
            fileName =  MD5Encoder.encode(url);    //这里加不加密无所谓
            File file=new File(mContext.getCacheDir(),fileName);
            if (!file.exists()){
                return null;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从网络获取图片后,保存至本地缓存
     * @param url
     * @param bitmap
     */
    public void setBitmapToLocal(String url,Bitmap bitmap){
        try {

            String fileName =  MD5Encoder.encode(url);//把图片的url当做文件名,并进行MD5加密
            File file=new File(mContext.getCacheDir(),fileName);

            //通过得到文件的父文件,判断父文件是否存在
            File parentFile = file.getParentFile();
            if (!parentFile.exists()){
                parentFile.mkdirs();
            }
            //把图片保存至本地
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
