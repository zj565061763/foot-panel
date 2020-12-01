package com.sd.lib.foot_panel.ext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 键盘监听
 */
abstract class BaseKeyboardListener
{
    protected final Activity mActivity;
    private View mTarget;

    private InternalPopupWindow mPopupWindow;
    /** View最大高度 */
    private int mMaxViewHeight;

    /** 当前键盘高度 */
    private int mKeyboardHeight;
    /** 键盘可见时候的高度 */
    private int mKeyboardVisibleHeight;
    /** 缓存的键盘可见时候的高度 */
    private static int sCachedKeyboardVisibleHeight;

    public BaseKeyboardListener(Activity activity)
    {
        if (activity == null)
            throw new NullPointerException("activity is null");

        mActivity = activity;
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

    /**
     * 开始监听
     *
     * @param window
     */
    public final void start(Window window)
    {
        if (window == null)
            return;

        final View target = window.getDecorView();
        if (setTarget(target))
            hidePopupWindow();

        showPopupWindow(target);
    }

    private boolean setTarget(View target)
    {
        final View old = mTarget;
        if (old != target)
        {
            if (old != null)
                old.removeOnAttachStateChangeListener(mOnAttachStateChangeListener);

            mTarget = target;

            if (target != null)
                target.addOnAttachStateChangeListener(mOnAttachStateChangeListener);

            return true;
        }
        return false;
    }

    private final View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener()
    {
        @Override
        public void onViewAttachedToWindow(View v)
        {
            showPopupWindow(v);
        }

        @Override
        public void onViewDetachedFromWindow(View v)
        {
            hidePopupWindow();
        }
    };

    /**
     * 显示PopupWindow
     *
     * @param target
     * @return
     */
    private boolean showPopupWindow(View target)
    {
        if (target == null)
            return false;

        if (mActivity.isFinishing())
            return false;

        if (mPopupWindow != null && mPopupWindow.isShowing())
            return false;

        if (mPopupWindow == null)
            mPopupWindow = new InternalPopupWindow(mActivity);

        try
        {
            mPopupWindow.showAtLocation(target, Gravity.NO_GRAVITY, 0, 0);
            return true;
        } catch (Exception e)
        {
            mPopupWindow = null;
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 隐藏PopupWindow
     */
    private void hidePopupWindow()
    {
        if (mPopupWindow != null)
        {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    protected void onStart()
    {
    }

    protected void onStop()
    {
    }

    /**
     * 键盘高度变化
     *
     * @param height
     */
    protected abstract void onKeyboardHeightChanged(int height);

    private final class InternalPopupWindow extends PopupWindow implements View.OnAttachStateChangeListener, ViewTreeObserver.OnGlobalLayoutListener
    {
        private final View mView;
        private final Rect mRect = new Rect();

        public InternalPopupWindow(Context context)
        {
            mView = new View(context.getApplicationContext());
            mView.addOnAttachStateChangeListener(InternalPopupWindow.this);

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
                observer.addOnGlobalLayoutListener(InternalPopupWindow.this);

            BaseKeyboardListener.this.onStart();
        }

        @Override
        public void onViewDetachedFromWindow(View v)
        {
            final ViewTreeObserver observer = v.getViewTreeObserver();
            if (observer.isAlive())
                observer.removeOnGlobalLayoutListener(InternalPopupWindow.this);

            BaseKeyboardListener.this.onStop();
        }

        @Override
        public void onGlobalLayout()
        {
            mView.getWindowVisibleDisplayFrame(mRect);

            final int height = mRect.height();
            if (height > mMaxViewHeight)
                mMaxViewHeight = height;

            int keyboardHeight = mMaxViewHeight - height;
            if (keyboardHeight > 0 && keyboardHeight <= 10)
            {
                // 如果键盘高度过小，则当作0处理
                keyboardHeight = 0;
            }

            if (mKeyboardHeight != keyboardHeight)
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
}
