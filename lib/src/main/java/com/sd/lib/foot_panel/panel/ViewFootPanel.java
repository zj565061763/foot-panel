package com.sd.lib.foot_panel.panel;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * View面板
 */
public class ViewFootPanel extends BaseFootPanel {
    private final View mView;

    public ViewFootPanel(@NonNull View view) {
        mView = view;
    }

    @Override
    public int getPanelHeight() {
        return mView.getHeight();
    }

    @Override
    public void initPanel(@NonNull HeightChangeCallback callback) {
        super.initPanel(callback);
        mView.removeOnLayoutChangeListener(mOnLayoutChangeListener);
        mView.addOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    private final View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (v == mView) {
                final int oldHeight = oldBottom - oldTop;
                final int height = bottom - top;
                if (oldHeight != height) {
                    notifyHeight(height);
                }
            }
        }
    };

    @Override
    public void releasePanel() {
        super.releasePanel();
        mView.removeOnLayoutChangeListener(mOnLayoutChangeListener);
    }
}
