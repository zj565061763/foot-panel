package com.sd.lib.foot_panel.panel;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public abstract class BaseFootPanel implements IFootPanel {
    private HeightChangeCallback mHeightChangeCallback;

    /**
     * 通知高度
     */
    protected final void notifyHeight(int height) {
        if (mHeightChangeCallback != null) {
            mHeightChangeCallback.onHeightChanged(height, this);
        }
    }

    @CallSuper
    @Override
    public void initPanel(@NonNull HeightChangeCallback callback) {
        mHeightChangeCallback = callback;
    }

    @CallSuper
    @Override
    public void releasePanel() {
        mHeightChangeCallback = null;
    }
}
