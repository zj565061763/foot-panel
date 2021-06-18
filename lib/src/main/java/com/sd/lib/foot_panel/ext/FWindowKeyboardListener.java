package com.sd.lib.foot_panel.ext;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * 键盘监听
 */
public abstract class FWindowKeyboardListener {
    private static final String TAG = FWindowKeyboardListener.class.getSimpleName();

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
    public final boolean start(@NonNull Window window) {
        final View target = window.getDecorView();
        if (target == null) {
            return false;
        }

        Log.i(TAG, "start window:" + window);
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
        Log.i(TAG, "stop");
        setTarget(null);
        hidePopupWindow();
    }

    private View getTarget() {
        return mTarget == null ? null : mTarget.get();
    }

    private boolean setTarget(View target) {
        final View old = getTarget();
        if (old == target) {
            return false;
        }

        if (old != null) {
            old.removeOnAttachStateChangeListener(mOnAttachStateChangeListener);
        }

        mTarget = target == null ? null : new WeakReference<>(target);
        Log.i(TAG, "setTarget target:" + target);

        if (target != null) {
            target.addOnAttachStateChangeListener(mOnAttachStateChangeListener);
        }
        return true;
    }

    private final View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
            Log.i(TAG, "target onViewAttachedToWindow " + v);
            showPopupWindow();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            Log.i(TAG, "target onViewDetachedFromWindow " + v);
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

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            return false;
        }

        if (mPopupWindow == null) {
            mPopupWindow = new InternalPopupWindow(target.getContext());
        }

        Log.i(TAG, "showPopupWindow target:" + target);
        mPopupWindow.showAtLocation(target, Gravity.NO_GRAVITY, 0, 0);
        return true;
    }

    /**
     * 隐藏PopupWindow
     */
    private void hidePopupWindow() {
        if (mPopupWindow != null) {
            Log.i(TAG, "hidePopupWindow");
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    /**
     * 通知键盘高度
     */
    final void notifyKeyboardHeight(int height) {
        if (mKeyboardHeight != height) {
            mKeyboardHeight = height;
            Log.i(TAG, "notifyKeyboardHeight height:" + height);

            if (height > 0) {
                mKeyboardVisibleHeight = height;
            }
            onKeyboardHeightChanged(height);
        }
    }

    /**
     * 开始监听
     */
    protected void onStart() {
        Log.i(TAG, "onStart");
    }

    /**
     * 停止监听
     */
    protected void onStop() {
        Log.i(TAG, "onStop");
    }

    /**
     * 键盘高度变化
     */
    protected abstract void onKeyboardHeightChanged(int height);

    private final class InternalPopupWindow extends PopupWindow implements View.OnAttachStateChangeListener {
        private final View mView;

        public InternalPopupWindow(Context context) {
            mView = new View(context.getApplicationContext());
            mView.addOnAttachStateChangeListener(InternalPopupWindow.this);
            mView.addOnLayoutChangeListener(mOnLayoutChangeListener);

            setContentView(mView);
            setWidth(1);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            setInputMethodMode(INPUT_METHOD_NEEDED);
        }

        @Override
        public void onViewAttachedToWindow(View v) {
            FWindowKeyboardListener.this.onStart();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            FWindowKeyboardListener.this.onStop();
        }

        private final View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                checkViewHeight(v.getHeight());
            }
        };

        /**
         * 检查View高度
         */
        private void checkViewHeight(int viewHeight) {
            if (viewHeight > mMaxViewHeight) {
                mMaxViewHeight = viewHeight;
            }

            int keyboardHeight = mMaxViewHeight - viewHeight;
            Log.i(TAG, "checkViewHeight"
                    + " mMaxViewHeight:" + mMaxViewHeight
                    + " viewHeight:" + viewHeight
                    + " keyboardHeight:" + keyboardHeight);

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
}