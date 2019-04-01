package com.yanshao.yanimageload.bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * @author WANGYAN
 * 微博：@Wang丶Yan
 * Github:https://github.com/yanshao
 * 创建时间：2019-04-01
 */
public class CancelableRequestDelegate {
    private Map<Integer, String> mCheckUnRealRequest = Collections
            .synchronizedMap(new HashMap<Integer, String>());


    public void putRequest(int hashCode, String cacheKey)
    {
        mCheckUnRealRequest.put(hashCode, cacheKey);
    }

    public String getCacheKey(int hashCode)
    {
        return mCheckUnRealRequest.get(hashCode);
    }

    public void remove(int hashCode)
    {
        mCheckUnRealRequest.remove(hashCode);
    }
}
