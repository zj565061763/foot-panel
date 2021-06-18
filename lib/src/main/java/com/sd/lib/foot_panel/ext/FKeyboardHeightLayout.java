package com.sd.lib.foot_panel.ext;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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

    protected int getKeyboardHeight(FKeyboardListener listener) {
        return listener.getKeyboardHeight();
    }

    /**
     * 键盘高度回调
     */
    private final FKeyboardListener.Callback mKeyboardCallback = new FKeyboardListener.Callback() {
        @Override
        public void onKeyboardHeightChanged(int height, FKeyboardListener listener) {
            final int viewHeight = getHeight();
            Log.i(FKeyboardHeightLayout.class.getSimpleName(), "onKeyboardHeightChanged height:" + height + " viewHeight:" + viewHeight);
            if (viewHeight != height) {
                requestLayout();
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int keyboardHeight = getKeyboardHeight(FKeyboardListener.of(mActivity));
        Log.i(FKeyboardHeightLayout.class.getSimpleName(), "onMeasure keyboardHeight:" + keyboardHeight);
        super.onMeasure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(keyboardHeight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FKeyboardListener.of(mActivity).addCallback(mKeyboardCallback);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FKeyboardListener.of(mActivity).removeCallback(mKeyboardCallback);
    }
}
