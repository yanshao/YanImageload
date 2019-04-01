package com.yanshao.yanimageload.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;

import com.yanshao.yanimageload.bean.ImageBean;
import com.yanshao.yanimageload.util.Dispatcher;
import com.yanshao.yanimageload.util.FileUtils;
import com.yanshao.yanimageload.util.MD5Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.BlockingQueue;
/**
 * 本地缓存线程
 * @author WANGYAN
 * 微博：@Wang丶Yan
 * Github:https://github.com/yanshao
 * 创建时间：2019-03-22
 */
public class LocalCacheUtils extends Dispatcher {

Context mContext;
    public LocalCacheUtils(Context context, BlockingQueue<ImageBean> mqueue, Handler handler) {
        super(context, mqueue, handler,
                YanImageLoad.MSG_LOCAL_GET_SUCCESS,
                YanImageLoad.MSG_LOCAL_GET_ERROR);
        mContext=context;
    }




    @Override
    protected void dealRequest(ImageBean request) {
      Bitmap bitmap=  getBitmapFromLocal(request.getUrl());
      if (bitmap==null){
          sendErrorMsg(request);
      }else{
          bitmap = FileUtils.compress(bitmap, request.getImageview());
          request.setBitmap(bitmap);
          YanImageLoad.setBitmapToMemory(request.getUrl(), bitmap);
          sendSuccessMsg(request);
      }
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
}
