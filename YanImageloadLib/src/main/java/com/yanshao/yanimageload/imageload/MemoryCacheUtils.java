package com.yanshao.yanimageload.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.LruCache;

import com.yanshao.yanimageload.bean.ImageBean;
import com.yanshao.yanimageload.util.Dispatcher;
import com.yanshao.yanimageload.util.FileUtils;

import java.util.concurrent.BlockingQueue;
/**
 * 内存缓存
 * @author WANGYAN
 * 微博：@Wang丶Yan
 * Github:https://github.com/yanshao
 * 创建时间：2019-03-22
 */
public class MemoryCacheUtils extends Dispatcher {
    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCacheUtils(Context context, LruCache<String, Bitmap> mMemoryCache, BlockingQueue<ImageBean> cacheQueue, Handler uiHandler) {
        super(context, cacheQueue, uiHandler, YanImageLoad.MSG_CACHE_HINT, YanImageLoad.MSG_CACHE_UN_HINT);
        this.mMemoryCache = mMemoryCache;
    }

    /**
     * 从内存中读图片
     *
     * @param url
     */
    public Bitmap getBitmapFromMemory(String url) {

        if (url == null || "".equals(url)) {
            return null;
        }
        Bitmap bitmap = mMemoryCache.get(url);
        return bitmap;

    }


    @Override
    protected void dealRequest(ImageBean request) {
        Bitmap bitmap = getBitmapFromMemory(request.getUrl());
        if (bitmap == null) {
            sendErrorMsg(request);
        } else {
            bitmap = FileUtils.compress(bitmap, request.getImageview());
            request.setBitmap(bitmap);
            sendSuccessMsg(request);
        }
    }
}
