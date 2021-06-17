package com.sd.lib.foot_panel.panel;

import android.view.View;

/**
 * View面板
 */
public class ViewFootPanel extends BaseFootPanel {
    private final View mView;

    public ViewFootPanel(View view) {
        if (view == null) {
            throw new NullPointerException("view is null");
        }
        mView = view;
    }

    private final View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            final HeightChangeCallback callback = getHeightChangeCallback();
            if (callback == null) {
                releasePanel();
                return;
            }

            if (v == mView) {
                final int oldHeight = oldBottom - oldTop;
                final int height = bottom - top;
                if (oldHeight != height) {
                    callback.onHeightChanged(height);
                }
            }
        }
    };

    @Override
    public int getPanelHeight() {
        return mView.getHeight();
    }

    @Override
    public void initPanel(HeightChangeCallback callback) {
        super.initPanel(callback);
        mView.addOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    @Override
    public void releasePanel() {
        super.releasePanel();
        mView.removeOnLayoutChangeListener(mOnLayoutChangeListener);
    }
}
