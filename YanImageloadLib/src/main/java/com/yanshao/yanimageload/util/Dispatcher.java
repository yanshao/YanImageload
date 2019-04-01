package com.yanshao.yanimageload.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.widget.ImageView;

import com.yanshao.yanimageload.bean.ImageBean;

import java.util.concurrent.BlockingQueue;

/**
 * Created by zhy on 15/7/31.
 */
public abstract class Dispatcher extends Thread
{
    protected Context mContext;
    protected BlockingQueue<ImageBean> mQueue;
    protected volatile boolean mQuit = false;
    protected Handler mUiHandler;

    protected int mMsgSuccessWhat;
    protected int mMsgFailWhat;

    public Dispatcher(Context context, BlockingQueue<ImageBean> queue, Handler uiHandler, int msgSuccessWhat, int msgFailWhat)
    {
        mContext = context;
        mQueue = queue;
        mUiHandler = uiHandler;

        mMsgSuccessWhat = msgSuccessWhat;
        mMsgFailWhat = msgFailWhat;

    }

    public void quit()
    {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run()
    {

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true)
        {
            try
            {
                ImageBean request = mQueue.take();
                if (request.checkTaskNotActual())
                    continue;

                dealRequest(request);

            } catch (InterruptedException e)
            {
                // 如果要求退出则退出，否则遇到异常继续
                if (mQuit)
                    return;
                else
                    continue;
            }
        }
    }

    protected void sendErrorMsg(ImageBean request)
    {
        sendErrorMsg(request, mMsgFailWhat);
    }

    protected void sendSuccessMsg(ImageBean request)
    {
        sendSuccessMsg(request, mMsgSuccessWhat);
    }

    protected abstract void dealRequest(ImageBean request);

    protected void sendSuccessMsg(ImageBean request, int what)
    {
        Message msg = Message.obtain(null, what);
        msg.obj = request;
        mUiHandler.sendMessage(msg);
    }

    protected void sendErrorMsg(ImageBean request, int what)
    {
        Message msg = Message.obtain(null, what);
        msg.obj = request;
        mUiHandler.sendMessage(msg);
    }


}
