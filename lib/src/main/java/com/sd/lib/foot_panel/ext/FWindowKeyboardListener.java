package com.sd.lib.foot_panel.ext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

/**
 * 键盘监听
 */
public abstract class FWindowKeyboardListener {
    private InternalPopupWindow mPopupWindow;
    private WeakReference<View> mTarget;

    /** View最大高度 */
    private int mMaxViewHeight;

    /** 当前键盘高度 */
    private int mKeyboardHeight;
    /** 键盘可见时候的高度 */
    private int mKeyboardVisibleHeight;
    /** 缓存的键盘可见时候的高度 */
    private static int sCachedKeyboardVisibleHeight;

    /**
     * 当前键盘高度
     */
    public int getKeyboardHeight() {
        return mKeyboardHeight;
    }

    /**
     * 键盘可见时候的高度
     */
    public int getKeyboardVisibleHeight() {
        return mKeyboardVisibleHeight;
    }

    /**
     * 缓存的键盘可见时候的高度
     */
    public static int getCachedKeyboardVisibleHeight() {
        return sCachedKeyboardVisibleHeight;
    }

    /**
     * 开始监听
     */
    public final boolean start(Window window) {
        if (window == null) {
            return false;
        }

        final View target = window.getDecorView();
        if (target == null) {
            return false;
        }

        if (isFinishing(target)) {
            return false;
        }

        if (setTarget(target)) {
            hidePopupWindow();
        }

        showPopupWindow();
        return true;
    }

    /**
     * 停止监听
     */
    public final void stop() {
        hidePopupWindow();
        setTarget(null);
    }

    private View getTarget() {
        return mTarget == null ? null : mTarget.get();
    }

    private boolean setTarget(View target) {
        final View old = getTarget();
        if (old != target) {
            if (old != null) {
                old.removeOnAttachStateChangeListener(mOnAttachStateChangeListener);
            }

            mTarget = target == null ? null : new WeakReference<>(target);

            if (target != null) {
                target.addOnAttachStateChangeListener(mOnAttachStateChangeListener);
            }
            return true;
        }
        return false;
    }

    private final View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
            showPopupWindow();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            hidePopupWindow();
        }
    };

    /**
     * 显示PopupWindow
     */
    private boolean showPopupWindow() {
        final View target = getTarget();
        if (!isAttached(target)) {
            return false;
        }

        if (isFinishing(target)) {
            return false;
        }

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            return false;
        }

        if (mPopupWindow == null) {
            mPopupWindow = new InternalPopupWindow(target.getContext());
        }

        mPopupWindow.showAtLocation(target, Gravity.NO_GRAVITY, 0, 0);
        return true;
    }

    /**
     * 隐藏PopupWindow
     */
    private void hidePopupWindow() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    protected void onStart() {
    }

    protected void onStop() {
    }

    /**
     * 通知键盘高度
     */
    final void notifyKeyboardHeight(int height) {
        if (mKeyboardHeight != height) {
            mKeyboardHeight = height;
            if (height > 0) {
                mKeyboardVisibleHeight = height;
            }
            onKeyboardHeightChanged(height);
        }
    }

    /**
     * 键盘高度变化
     */
    protected abstract void onKeyboardHeightChanged(int height);

    private final class InternalPopupWindow extends PopupWindow implements View.OnAttachStateChangeListener, ViewTreeObserver.OnGlobalLayoutListener {
        private final View mView;
        private final Rect mRect = new Rect();

        public InternalPopupWindow(Context context) {
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
        public void onViewAttachedToWindow(View v) {
            final ViewTreeObserver observer = v.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.addOnGlobalLayoutListener(InternalPopupWindow.this);
            }
            FWindowKeyboardListener.this.onStart();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            final ViewTreeObserver observer = v.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.removeOnGlobalLayoutListener(InternalPopupWindow.this);
            }
            FWindowKeyboardListener.this.onStop();
        }

        @Override
        public void onGlobalLayout() {
            mView.getWindowVisibleDisplayFrame(mRect);

            final int viewHeight = mRect.height();
            if (viewHeight > mMaxViewHeight) {
                mMaxViewHeight = viewHeight;
            }

            int keyboardHeight = mMaxViewHeight - viewHeight;
            if (keyboardHeight > 0 && keyboardHeight <= 100) {
                // 如果键盘高度过小，则当作0处理
                keyboardHeight = 0;
            }

            if (keyboardHeight > 0) {
                // 缓存键盘可见时候的高度
                sCachedKeyboardVisibleHeight = keyboardHeight;
            }

            notifyKeyboardHeight(keyboardHeight);
        }
    }

    private static boolean isAttached(View view) {
        if (view == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            return view.isAttachedToWindow();
        } else {
            return view.getWindowToken() != null;
        }
    }

    private static boolean isFinishing(View view) {
        final Context context = view.getContext();
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            return activity.isFinishing();
        }
        return false;
    }
}
