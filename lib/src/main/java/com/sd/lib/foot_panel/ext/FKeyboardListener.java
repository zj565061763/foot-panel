package com.sd.lib.foot_panel.ext;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 键盘监听
 */
public class FKeyboardListener
{
    private final Activity mActivity;
    private final FWindowKeyboardListener mKeyboardListener;
    private final Map<Callback, String> mCallbackHolder = new WeakHashMap<>();

    private FKeyboardListener(Activity activity)
    {
        if (activity == null)
            throw new NullPointerException("activity is null");

        mActivity = activity;
        mKeyboardListener = new FWindowKeyboardListener(activity)
        {
            @Override
            protected void onKeyboardHeightChanged(int height)
            {
                FKeyboardListener.this.onKeyboardHeightChanged(height);
            }
        };
    }

    /**
     * 添加回调，内部用弱引用保存
     *
     * @param callback
     */
    public void addCallback(Callback callback)
    {
        if (callback == null)
            return;

        mCallbackHolder.put(callback, "");
    }

    /**
     * 移除回调
     *
     * @param callback
     */
    public void removeCallback(Callback callback)
    {
        if (callback == null)
            return;

        mCallbackHolder.remove(callback);
    }

    /**
     * 返回当前软键盘高度，如果当前软键盘不可见，则返回0
     *
     * @return
     */
    public int getKeyboardHeight()
    {
        return mKeyboardListener.getKeyboardHeight();
    }

    /**
     * 返回软键盘可见时候的高度
     *
     * @return
     */
    public int getKeyboardVisibleHeight()
    {
        return mKeyboardListener.getKeyboardVisibleHeight();
    }

    /**
     * 缓存的键盘可见时候的高度
     *
     * @return
     */
    public static int getCachedKeyboardVisibleHeight()
    {
        return FWindowKeyboardListener.getCachedKeyboardVisibleHeight();
    }

    /**
     * 键盘高度变化
     *
     * @param height
     */
    private void onKeyboardHeightChanged(int height)
    {
        final List<Callback> list = new ArrayList<>(mCallbackHolder.keySet());
        for (Callback item : list)
        {
            item.onKeyboardHeightChanged(height, this);
        }
    }

    /**
     * 开始监听
     */
    public boolean start()
    {
        final Window window = mActivity.getWindow();
        final boolean start = mKeyboardListener.start(window);
        if (start)
        {
            final Application application = mActivity.getApplication();
            application.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            application.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        }
        return start;
    }

    private final Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks()
    {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState)
        {
        }

        @Override
        public void onActivityStarted(Activity activity)
        {
        }

        @Override
        public void onActivityResumed(Activity activity)
        {
        }

        @Override
        public void onActivityPaused(Activity activity)
        {
        }

        @Override
        public void onActivityStopped(Activity activity)
        {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState)
        {
        }

        @Override
        public void onActivityDestroyed(Activity activity)
        {
            if (mActivity == activity)
            {
                activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                removeActivity(activity);
            }
        }
    };

    public interface Callback
    {
        /**
         * 键盘高度变化回调
         *
         * @param height
         * @param listener
         */
        void onKeyboardHeightChanged(int height, FKeyboardListener listener);
    }

    //---------- static ----------

    private static final Map<Activity, FKeyboardListener> MAP_LISTENER = new WeakHashMap<>();

    public static synchronized FKeyboardListener of(Activity activity)
    {
        if (activity == null)
            return null;

        FKeyboardListener listener = MAP_LISTENER.get(activity);
        if (listener == null)
        {
            listener = new FKeyboardListener(activity);
            if (listener.start())
                MAP_LISTENER.put(activity, listener);
        }
        return listener;
    }

    private static synchronized void removeActivity(Activity activity)
    {
        if (activity == null)
            return;

        MAP_LISTENER.remove(activity);
    }
}
