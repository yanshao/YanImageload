package com.yanshao.yanimageload.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.yanshao.yanimageload.bean.ImageBean;
import com.yanshao.yanimageload.util.CircleImageDrawable;
import com.yanshao.yanimageload.util.Dispatcher;
import com.yanshao.yanimageload.util.FileUtils;
import com.yanshao.yanimageload.util.LIFOLinkedBlockingDeque;
import com.yanshao.yanimageload.util.RoundImageDrawable;
import com.yanshao.yanimageload.util.Scheme;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 网络获取图片
 * @author WANGYAN
 * 微博：@Wang丶Yan
 * Github:https://github.com/yanshao
 * 创建时间：2019-03-22
 */
public class NetCacheUtils extends Dispatcher {

    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private ExecutorService mThreadPool;
    private static final int DEAFULT_THREAD_COUNT = 3;
    protected static final int MAX_REDIRECT_COUNT = 5;

    public NetCacheUtils(Context context, LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils, BlockingQueue<ImageBean> queue, Handler handler) {
        super(context, queue, handler, YanImageLoad.MSG_CACHE_HINT, YanImageLoad.MSG_CACHE_UN_HINT);
        mLocalCacheUtils = localCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
        mThreadPool = new ThreadPoolExecutor(DEAFULT_THREAD_COUNT, DEAFULT_THREAD_COUNT, 0, TimeUnit.MILLISECONDS, new LIFOLinkedBlockingDeque<Runnable>());
    }

    @Override
    protected void dealRequest(ImageBean request) {
        mThreadPool.execute(buildTask(request));
    }

    private Runnable buildTask(final ImageBean request) {
        return new Runnable() {
            @Override
            public void run() {

                switch (Scheme.ofUri(request.getUrl())) {
                    case HTTP:
                        Bitmap bitmap = downLoadBitmap(request);
                        if (bitmap == null) {
                            sendErrorMsg(request);
                        } else {
                            bitmap = FileUtils.compress(bitmap, request.getImageview());
                            request.setBitmap(bitmap);
                            YanImageLoad.setBitmapToMemory(request.getUrl(), bitmap);
                            FileUtils.setBitmapToLocal(mContext,request.getUrl(), bitmap);
                            sendSuccessMsg(request);
                        }
                        break;
                    case PATH:
                        Bitmap bitmap1 = getfileBitmap(request);
                        if (bitmap1 == null) {
                            Log.e("yy","bitmap1"+bitmap1);
                            sendErrorMsg(request);
                        } else {
                            bitmap1 = FileUtils.compress(bitmap1, request.getImageview());
                            request.setBitmap(bitmap1);
                            YanImageLoad.setBitmapToMemory(request.getUrl(), bitmap1);
                            FileUtils.setBitmapToLocal(mContext,request.getUrl(), bitmap1);
                            sendSuccessMsg(request);
                        }
                        break;
                    default:
                        sendErrorMsg(request);

                }

            }
        };
    }


    public Bitmap getfileBitmap(ImageBean request) {
        File file = new File(request.getUrl());
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            Log.e("yy","bitmapE111W");
            if (bitmap != null) {
                Log.e("yy","bitmap"+bitmap);
                bitmap = FileUtils.compress(bitmap, request.getImageview());

                return bitmap;
            }
        } catch (FileNotFoundException e) {
            Log.e("yy","bitmapEW");
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 网络下载图片
     *
     * @return
     */
    public Bitmap downLoadBitmap(ImageBean imageBean) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(imageBean.getUrl()).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                //图片压缩
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;//宽高压缩为原来的1/2
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;

                //Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream(),null,options);
                Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception e) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }
}


