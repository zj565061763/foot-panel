package com.sd.lib.foot_panel.ext;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

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

    private InternalPopupWindow mPopupWindow;
    private final Rect mRect = new Rect();

    private int mWindowHeight;
    private int mMaxWindowHeight;

    /** 当前键盘高度 */
    private int mKeyboardHeight;
    /** 键盘可见时候的高度 */
    private int mKeyboardVisibleHeight;
    /** 缓存的键盘可见时候的高度 */
    private static int sCachedKeyboardVisibleHeight;

    private final Map<Callback, String> mCallbackHolder = new WeakHashMap<>();

    private FKeyboardListener(Activity activity)
    {
        if (activity == null)
            throw new NullPointerException("activity is null");

        mActivity = activity;
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
        return mKeyboardHeight;
    }

    /**
     * 返回软键盘可见时候的高度
     *
     * @return
     */
    public int getKeyboardVisibleHeight()
    {
        return mKeyboardVisibleHeight;
    }

    /**
     * 缓存的键盘可见时候的高度
     *
     * @return
     */
    public static int getCachedKeyboardVisibleHeight()
    {
        return sCachedKeyboardVisibleHeight;
    }

    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
    {
        @Override
        public void onGlobalLayout()
        {
            if (mPopupWindow == null)
                return;

            mPopupWindow.mView.getWindowVisibleDisplayFrame(mRect);

            final int old = mWindowHeight;
            final int height = mRect.height();
            if (old != height)
            {
                mWindowHeight = height;
                onWindowHeightChanged(height);

                if (height > mMaxWindowHeight)
                    mMaxWindowHeight = height;

                final int oldKeyboardHeight = mKeyboardHeight;
                final int keyboardHeight = mMaxWindowHeight - height;
                if (oldKeyboardHeight != keyboardHeight)
                {
                    mKeyboardHeight = keyboardHeight;
                    if (keyboardHeight > 0)
                    {
                        mKeyboardVisibleHeight = keyboardHeight;
                        sCachedKeyboardVisibleHeight = keyboardHeight;
                    }

                    onKeyboardHeightChanged(keyboardHeight);
                }
            }
        }
    };

    private View getTarget()
    {
        return mActivity.findViewById(Window.ID_ANDROID_CONTENT);
    }

    /**
     * 开始监听
     */
    public final void start()
    {
        if (mActivity.isFinishing())
            return;

        final Application application = mActivity.getApplication();
        application.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        application.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);

        getTarget().removeCallbacks(mShowRunnable);
        getTarget().post(mShowRunnable);
    }

    /**
     * 停止监听
     */
    public final void stop()
    {
        final Application application = mActivity.getApplication();
        application.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);

        getTarget().removeCallbacks(mShowRunnable);

        if (mPopupWindow != null)
        {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    private final Runnable mShowRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (mActivity.isFinishing())
                return;

            if (mPopupWindow == null)
                mPopupWindow = new InternalPopupWindow(mActivity);

            try
            {
                mPopupWindow.showAtLocation(getTarget(), Gravity.NO_GRAVITY, 0, 0);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    private void onWindowHeightChanged(int height)
    {
        // private 暂不开放
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

    private final class InternalPopupWindow extends PopupWindow implements View.OnAttachStateChangeListener
    {
        private final View mView;

        public InternalPopupWindow(Context context)
        {
            mView = new View(context);
            mView.addOnAttachStateChangeListener(this);

            setContentView(mView);
            setWidth(1);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            setInputMethodMode(INPUT_METHOD_NEEDED);
        }

        @Override
        public void onViewAttachedToWindow(View v)
        {
            final ViewTreeObserver observer = v.getViewTreeObserver();
            if (observer.isAlive())
                observer.addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }

        @Override
        public void onViewDetachedFromWindow(View v)
        {
            final ViewTreeObserver observer = v.getViewTreeObserver();
            if (observer.isAlive())
                observer.removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
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

    private static final Map<Activity, FKeyboardListener> MAP_LISTENER = new WeakHashMap<>();

    public static synchronized FKeyboardListener of(Activity activity)
    {
        if (activity == null)
            return null;

        FKeyboardListener listener = MAP_LISTENER.get(activity);
        if (listener == null)
        {
            listener = new FKeyboardListener(activity);
            if (!activity.isFinishing())
            {
                MAP_LISTENER.put(activity, listener);
                listener.start();
            }
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
