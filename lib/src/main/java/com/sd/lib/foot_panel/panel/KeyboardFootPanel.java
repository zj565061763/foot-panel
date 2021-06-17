package com.sd.lib.foot_panel.panel;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.sd.lib.foot_panel.ext.FKeyboardListener;

/**
 * 软键盘面板
 */
public class KeyboardFootPanel extends BaseFootPanel {
    private final Activity mActivity;
    private FKeyboardListener mKeyboardListener;

    public KeyboardFootPanel(@NonNull Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getPanelHeight() {
        final FKeyboardListener listener = mKeyboardListener;
        return listener == null ? 0 : listener.getKeyboardHeight();
    }

    @Override
    public void initPanel(HeightChangeCallback callback) {
        super.initPanel(callback);
        if (mKeyboardListener == null) {
            mKeyboardListener = FKeyboardListener.of(mActivity);
            mKeyboardListener.addCallback(mKeyboardCallback);
        }
    }

    /**
     * 监听软键盘
     */
    private final FKeyboardListener.Callback mKeyboardCallback = new FKeyboardListener.Callback() {
        @Override
        public void onKeyboardHeightChanged(int height, FKeyboardListener listener) {
            notifyHeight(height);
        }
    };

    @Override
    public void releasePanel() {
        super.releasePanel();
        if (mKeyboardListener != null) {
            mKeyboardListener.removeCallback(mKeyboardCallback);
            mKeyboardListener = null;
        }
    }
}
