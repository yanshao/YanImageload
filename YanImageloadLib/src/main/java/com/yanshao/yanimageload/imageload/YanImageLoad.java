package com.yanshao.yanimageload.imageload;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import android.widget.ImageView;

import com.yanshao.yanimageload.bean.CancelableRequestDelegate;
import com.yanshao.yanimageload.bean.ImageBean;

import com.yanshao.yanimageload.util.CircleImageDrawable;
import com.yanshao.yanimageload.util.LIFOLinkedBlockingDeque;
import com.yanshao.yanimageload.util.LogUtils;
import com.yanshao.yanimageload.util.RoundImageDrawable;
import com.yanshao.yanimageload.util.Scheme;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 加载图片  目前仅支持 加载sd卡图片  和网络图片
 *
 * @author WANGYAN
 * 微博：@Wang丶Yan
 * Github:https://github.com/yanshao
 * 创建时间：2019-03-21
 */
public class YanImageLoad {
    private static Context mContext;
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private static YanImageLoad instance;
    ExecutorService priorityThreadPool = Executors.newFixedThreadPool(1);
    private CancelableRequestDelegate mCancelableRequestDelegate = new CancelableRequestDelegate();
    BlockingQueue<ImageBean> mCacheQueue;
    BlockingQueue<ImageBean> mLocalQueue;
    BlockingQueue<ImageBean> mNetworkQueue;
    public static final int MSG_CACHE_HINT = 0x110;//内存 成功 code
    public static final int MSG_CACHE_UN_HINT = MSG_CACHE_HINT + 1;//内存 失败 code
    public static final int MSG_HTTP_GET_ERROR = MSG_CACHE_UN_HINT + 1;//网络 成功 code
    public static final int MSG_HTTP_GET_SUCCESS = MSG_HTTP_GET_ERROR + 1;//网络 失败 code
    public static final int MSG_LOCAL_GET_SUCCESS = MSG_HTTP_GET_SUCCESS + 1;//本地（磁盘）成功 code
    public static final int MSG_LOCAL_GET_ERROR = MSG_LOCAL_GET_SUCCESS + 1;//本地（磁盘）失败 code
    public static LruCache<String, Bitmap> mMemoryCache;

    public CancelableRequestDelegate getCancelableRequestDelegate() {
        return mCancelableRequestDelegate;
    }

    private Handler handler = new YanHandler();


    private class YanHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            ImageBean imageBean = (ImageBean) msg.obj;

            switch (msg.what) {
                case MSG_CACHE_UN_HINT:
                    LogUtils.e("yy", "=内存加载失败=");
                    mLocalQueue.add(imageBean);

                    break;
                case MSG_LOCAL_GET_ERROR:
                    LogUtils.e("yy", "=本地加载失败=");
                    Log.e("yy", "leixing==" + Scheme.ofUri(imageBean.getUrl()));
                    Log.e("yy", "URL==" + imageBean.getUrl());
                    switch (Scheme.ofUri(imageBean.getUrl())) {

                        case HTTP:
                            mNetworkQueue.add(imageBean);
                            break;
                        default:
                            imageBean.setErrorImageRes();
                            break;
                    }


                    break;
                case MSG_HTTP_GET_ERROR:
                    LogUtils.e("yy", "=网络或者文件加载失败=");
                    imageBean.setErrorImageRes();
                    break;
                case MSG_CACHE_HINT:
                    LogUtils.e("yy", "=内存加载成功=");
                    imageBean.setResBitmap();
                    break;
                case MSG_LOCAL_GET_SUCCESS:
                    LogUtils.e("yy", "=本地加载成功=");
                    imageBean.setResBitmap();
                    break;
                case MSG_HTTP_GET_SUCCESS:
                    LogUtils.e("yy", "=网络或者文件加载成功=");
                    imageBean.setResBitmap();
                    break;
            }
        }
    }


    private YanImageLoad(Context context) {
        long maxMemory = Runtime.getRuntime().maxMemory() / 8;//得到手机最大允许内存的1/8,即超过指定内存,则开始回收
        //需要传入允许的内存最大值,虚拟机默认内存16M,真机不一定相同
        mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory) {
            //用于计算每个条目的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getByteCount();
                return byteCount;
            }
        };
        //内存加载
        mCacheQueue = new LIFOLinkedBlockingDeque<ImageBean>();
        mMemoryCacheUtils = new MemoryCacheUtils(context, mMemoryCache, mCacheQueue, handler);
        //本地缓存加载
        mLocalQueue = new LIFOLinkedBlockingDeque<ImageBean>();
        mLocalCacheUtils = new LocalCacheUtils(context, mLocalQueue, handler);
        //网络加载
        mNetworkQueue = new LinkedBlockingQueue<ImageBean>();
        mNetCacheUtils = new NetCacheUtils(context, mLocalCacheUtils, mMemoryCacheUtils, mNetworkQueue, handler);


        mLocalCacheUtils.start();
        mNetCacheUtils.start();
        mMemoryCacheUtils.start();

    }

    public static class Builder {
        private Context context;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        public YanImageLoad build() {
            Context context = this.context;

            mContext = context;
            return new YanImageLoad(context);
        }
    }

    public static YanImageLoad getInstance(Context context) {

        if (instance == null) {
            synchronized (YanImageLoad.class) {
                if (instance == null) {
                    instance = new Builder(context).build();
                }
            }
        }
        return instance;
    }

    /**
     * @param ivPic    imageview
     * @param url      图片url 或者路径
     * @param errresid 加载失败时显示的资源文件
     * @param tag      1  圆角  2  圆形  other  正常
     */
    public void disPlay(final ImageView ivPic, final String url, int errresid, final int tag) {

        ImageBean imageBean = new ImageBean(this, url, ivPic, errresid, tag);
        mCancelableRequestDelegate.putRequest(ivPic.hashCode(), imageBean.getCacheKey());

        priorityThreadPool.execute(new buildTask(imageBean));
    }

    public void disPlay(final ImageView ivPic, int resid, final int tag) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resid, null);
        if (tag == 1) {
            ivPic.setImageDrawable(new RoundImageDrawable(bitmap));
        } else if (tag == 2) {
            ivPic.setImageDrawable(new CircleImageDrawable(bitmap));
        } else {
            ivPic.setImageBitmap(bitmap);
        }

    }


    private class buildTask implements Runnable {

        ImageBean imageBean;

        public buildTask(ImageBean imageBean) {
            this.imageBean = imageBean;

        }

        @Override
        public void run() {
            mCacheQueue.offer(imageBean);
        }
    }


    /**
     * 往内存中写图片
     *
     * @param url
     * @param bitmap
     */
    public static void setBitmapToMemory(String url, Bitmap bitmap) {

        mMemoryCache.put(url, bitmap);
    }

}


