package com.sd.lib.foot_panel.ext;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Window;

import java.util.ArrayList;
import java.util.HashMap;
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

    private Map<Window, FWindowKeyboardListener> mCheckWindowHolder;

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
                FKeyboardListener.this.notifyCallbacks(height);
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
     * 通知回调对象
     *
     * @param height
     */
    private void notifyCallbacks(int height)
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
    private boolean start()
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

    /**
     * 停止监听
     */
    private void stop()
    {
        mKeyboardListener.stop();

        if (mCheckWindowHolder != null)
        {
            for (FWindowKeyboardListener item : mCheckWindowHolder.values())
            {
                item.stop();
            }
            mCheckWindowHolder.clear();
            mCheckWindowHolder = null;
        }

        final Application application = mActivity.getApplication();
        application.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    /**
     * 检查Window的键盘
     *
     * @param window
     */
    public void checkWindow(Window window)
    {
        if (window == null)
            return;

        if (mActivity.isFinishing())
            return;

        if (mActivity.getWindow() == window)
            throw new IllegalArgumentException("window must not be Activity's window");

        if (mCheckWindowHolder != null && mCheckWindowHolder.containsKey(window))
            return;

        final FWindowKeyboardListener keyboardListener = new FWindowKeyboardListener(mActivity)
        {
            @Override
            protected void onKeyboardHeightChanged(int height)
            {
                mKeyboardListener.notifyKeyboardHeight(height);
            }
        };

        if (keyboardListener.start(window))
        {
            if (mCheckWindowHolder == null)
                mCheckWindowHolder = new HashMap<>();
            mCheckWindowHolder.put(window, keyboardListener);
        }
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
                removeActivity(activity);
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

    private static final Map<Activity, FKeyboardListener> MAP_LISTENER = new HashMap<>();

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

        final FKeyboardListener listener = MAP_LISTENER.remove(activity);
        if (listener != null)
            listener.stop();
    }
}
