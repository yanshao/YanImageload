package com.yanshao.yanimageload.util;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
/**
 * 工具类
 * @author WANGYAN
 * 微博：@Wang丶Yan
 * Github:https://github.com/yanshao
 * 创建时间：2019-03-01
 */
public class Utils {
    /**
     * 根据ImageView获适当的压缩的宽和高
     *
     * @param imageView
     * @return
     */
    public static ImageSize getImageViewSize(View imageView)
    {

        ImageSize imageSize = new ImageSize();
        DisplayMetrics displayMetrics = imageView.getContext().getResources()
                .getDisplayMetrics();

        ViewGroup.LayoutParams lp = imageView.getLayoutParams();

        int width = imageView.getWidth();// 获取imageview的实际宽度
        if (width <= 0)
        {
            width = lp.width;// 获取imageview在layout中声明的宽度
        }
        if (width <= 0)
        {
            // width = imageView.getMaxWidth();// 检查最大值
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0)
        {
            width = displayMetrics.widthPixels;
        }

        int height = imageView.getHeight();// 获取imageview的实际高度
        if (height <= 0)
        {
            height = lp.height;// 获取imageview在layout中声明的宽度
        }
        if (height <= 0)
        {
            height = getImageViewFieldValue(imageView, "mMaxHeight");// 检查最大值
        }
        if (height <= 0)
        {
            height = displayMetrics.heightPixels;
        }
        imageSize.width = width;
        imageSize.height = height;

        return imageSize;
    }



    public static class ImageSize
    {
       public int width;
        public    int height;
        public ImageSize()
        {
        }

        public ImageSize(int width, int height)
        {
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString()
        {
            return "ImageSize{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    /**
     * 通过反射获取imageview的某个属性值
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName)
    {
        int value = 0;
        try
        {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
            {
                value = fieldValue;
            }
        } catch (Exception e)
        {
        }
        return value;

    }
}
