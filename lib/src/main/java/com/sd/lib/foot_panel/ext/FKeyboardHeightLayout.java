package com.sd.lib.foot_panel.ext;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 高度为键盘高度的布局
 */
public class FKeyboardHeightLayout extends FrameLayout {
    private Activity mActivity;

    public FKeyboardHeightLayout(Context context) {
        super(context);
        init(context);
    }

    public FKeyboardHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            throw new IllegalArgumentException("context must be instance of " + Activity.class.getName());
        }
    }

    protected int getKeyboardHeight() {
        return FKeyboardListener.of(mActivity).getKeyboardHeight();
    }

    /**
     * 键盘高度回调
     */
    private final FKeyboardListener.Callback mKeyboardCallback = new FKeyboardListener.Callback() {
        @Override
        public void onKeyboardHeightChanged(int height, FKeyboardListener listener) {
            updateViewHeight();
        }
    };

    private void updateViewHeight() {
        final ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            return;
        }

        final int keyboardHeight = getKeyboardHeight();
        if (params.height != keyboardHeight) {
            params.height = keyboardHeight;
            setLayoutParams(params);
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params != null) {
            params.height = getKeyboardHeight();
        }
        super.setLayoutParams(params);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateViewHeight();
        FKeyboardListener.of(mActivity).addCallback(mKeyboardCallback);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FKeyboardListener.of(mActivity).removeCallback(mKeyboardCallback);
    }
}
