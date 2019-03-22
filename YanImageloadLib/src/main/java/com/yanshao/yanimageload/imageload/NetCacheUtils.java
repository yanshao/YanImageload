package com.yanshao.yanimageload.imageload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.yanshao.yanimageload.util.CircleImageDrawable;
import com.yanshao.yanimageload.util.FileUtils;
import com.yanshao.yanimageload.util.RoundImageDrawable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetCacheUtils {

    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        mLocalCacheUtils = localCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
    }

    public NetCacheUtils() {

    }

    /**
     * 从网络下载图片
     *
     * @param ivPic 显示图片的imageview
     * @param url   下载图片的网络地址
     */
    public void getBitmapFromNet(ImageView ivPic, String url, int tag) {

        new BitmapTask().execute(ivPic, url, tag);//启动AsyncTask

    }


    public void getBitmapFromNet(View ivPic, String url) {

        new BitmapTask_view().execute(ivPic, url);//启动AsyncTask

    }

    public Bitmap getBitmapFromNet(final String url) {
        //启动AsyncTask
        return null;
    }

    /**
     * AsyncTask就是对handler和线程池的封装
     * 第一个泛型:参数类型
     * 第二个泛型:更新进度的泛型
     * 第三个泛型:onPostExecute的返回结果
     */
    class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

        private ImageView ivPic;
        private String url;
        private int tag;

        /**
         * 后台耗时操作,存在于子线程中
         *
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Object[] params) {
            ivPic = (ImageView) params[0];
            url = (String) params[1];
            tag = (int) params[2];
            return downLoadBitmap(url);
        }

        /**
         * 更新进度,在主线程中
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Void[] values) {
            super.onProgressUpdate(values);
        }

        /**
         * 耗时方法结束后执行该方法,主线程中
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
               // ivPic.setImageDrawable(BitmapUtil.RoundImage(result, mcontext));
                Bitmap bitmap=FileUtils.compress(result,ivPic);
                if (tag == 1) {
                    ivPic.setImageDrawable(new RoundImageDrawable(bitmap));
                } else if (tag == 2) {
                    ivPic.setImageDrawable(new CircleImageDrawable(bitmap));
                } else{
                    ivPic.setImageBitmap(bitmap);
                }
                //从网络获取图片后,保存至本地缓存
                mLocalCacheUtils.setBitmapToLocal(url, bitmap);
                //保存至内存中
                mMemoryCacheUtils.setBitmapToMemory(url, bitmap);

            }
        }
    }

    /**
     * AsyncTask就是对handler和线程池的封装
     * 第一个泛型:参数类型
     * 第二个泛型:更新进度的泛型
     * 第三个泛型:onPostExecute的返回结果
     */
    class BitmapTask_view extends AsyncTask<Object, Void, Bitmap> {

        private View ivPic;
        private String url;

        /**
         * 后台耗时操作,存在于子线程中
         *
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Object[] params) {
            ivPic = (View) params[0];
            url = (String) params[1];

            return downLoadBitmap(url);
        }

        /**
         * 更新进度,在主线程中
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Void[] values) {
            super.onProgressUpdate(values);
        }

        /**
         * 耗时方法结束后执行该方法,主线程中
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                //ivPic.setImageBitmap(result);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    //Android系统大于等于API16，使用setBackground
                    ivPic.setBackground(new BitmapDrawable(result));
                } else {
                    //Android系统小于API16，使用setBackground
                    ivPic.setBackgroundDrawable(new BitmapDrawable(result));
                }
                //从网络获取图片后,保存至本地缓存
                mLocalCacheUtils.setBitmapToLocal(url, result);
                //保存至内存中
                mMemoryCacheUtils.setBitmapToMemory(url, result);

            }
        }
    }

    /**
     * 网络下载图片
     *
     * @param url
     * @return
     */
    public Bitmap downLoadBitmap(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
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


