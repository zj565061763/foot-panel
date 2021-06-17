package com.sd.lib.foot_panel.ext;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 键盘监听
 */
public class FKeyboardListener {
    private final Activity mActivity;
    private final Map<Callback, String> mCallbackHolder = new WeakHashMap<>();
    private Map<Window, FWindowKeyboardListener> mCheckWindowHolder;

    private FKeyboardListener(@NonNull Activity activity) {
        mActivity = activity;
    }

    /**
     * 添加回调，弱引用保存
     */
    public void addCallback(Callback callback) {
        if (callback != null) {
            mCallbackHolder.put(callback, "");
        }
    }

    /**
     * 移除回调
     */
    public void removeCallback(Callback callback) {
        if (callback != null) {
            mCallbackHolder.remove(callback);
        }
    }

    /**
     * 当前键盘高度
     */
    public int getKeyboardHeight() {
        return mWindowKeyboardListener.getKeyboardHeight();
    }

    /**
     * 键盘可见时候的高度
     */
    public int getKeyboardVisibleHeight() {
        return mWindowKeyboardListener.getKeyboardVisibleHeight();
    }

    /**
     * 缓存的键盘可见时候的高度
     */
    public static int getCachedKeyboardVisibleHeight() {
        return FWindowKeyboardListener.getCachedKeyboardVisibleHeight();
    }

    /**
     * 键盘高度监听
     */
    private final FWindowKeyboardListener mWindowKeyboardListener = new FWindowKeyboardListener() {
        @Override
        protected void onKeyboardHeightChanged(int height) {
            final List<Callback> list = new ArrayList<>(mCallbackHolder.keySet());
            for (Callback item : list) {
                item.onKeyboardHeightChanged(height, FKeyboardListener.this);
            }
        }
    };

    /**
     * 开始监听
     */
    private boolean start() {
        final Window window = mActivity.getWindow();
        final boolean start = mWindowKeyboardListener.start(window);
        if (start) {
            final Application application = mActivity.getApplication();
            application.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            application.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        }
        return start;
    }

    /**
     * 停止监听
     */
    private void stop() {
        mWindowKeyboardListener.stop();

        if (mCheckWindowHolder != null) {
            for (FWindowKeyboardListener item : mCheckWindowHolder.values()) {
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
    public void checkWindow(Window window) {
        if (window == null) {
            return;
        }

        if (mActivity.isFinishing()) {
            return;
        }

        if (mActivity.getWindow() == window) {
            throw new IllegalArgumentException("window must not be Activity's window");
        }

        if (mCheckWindowHolder != null && mCheckWindowHolder.containsKey(window)) {
            return;
        }

        final FWindowKeyboardListener keyboardListener = new FWindowKeyboardListener() {
            @Override
            protected void onKeyboardHeightChanged(int height) {
                mWindowKeyboardListener.notifyKeyboardHeight(height);
            }
        };

        if (keyboardListener.start(window)) {
            if (mCheckWindowHolder == null) {
                mCheckWindowHolder = new WeakHashMap<>();
            }
            mCheckWindowHolder.put(window, keyboardListener);
        }
    }

    private final Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (mActivity == activity) {
                removeActivity(activity);
            }
        }
    };

    public interface Callback {
        /**
         * 键盘高度变化回调
         */
        void onKeyboardHeightChanged(int height, @NonNull FKeyboardListener listener);
    }

    //---------- static ----------

    private static final Map<Activity, FKeyboardListener> MAP_LISTENER = new HashMap<>();

    public static synchronized FKeyboardListener of(@NonNull Activity activity) {
        FKeyboardListener listener = MAP_LISTENER.get(activity);
        if (listener == null) {
            listener = new FKeyboardListener(activity);
            if (listener.start()) {
                MAP_LISTENER.put(activity, listener);
            }
        }
        return listener;
    }

    private static synchronized void removeActivity(@NonNull Activity activity) {
        final FKeyboardListener listener = MAP_LISTENER.remove(activity);
        if (listener != null) {
            listener.stop();
        }
    }
}
