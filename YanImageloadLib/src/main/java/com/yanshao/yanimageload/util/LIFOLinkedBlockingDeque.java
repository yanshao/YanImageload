package com.yanshao.yanimageload.util;

import java.util.concurrent.LinkedBlockingDeque;
/**
 * 先进后出线程池
 * @author WANGYAN
 * 微博：@Wang丶Yan
 * Github:https://github.com/yanshao
 * 创建时间：2019-04-01
 */
public class LIFOLinkedBlockingDeque<T> extends LinkedBlockingDeque<T> {

    private static final long serialVersionUID = -4114786347960826192L;


    @Override
    public boolean offer(T e) {
        return super.offerFirst(e);
    }


    @Override
    public T remove() {
        return super.removeFirst();
    }

}
