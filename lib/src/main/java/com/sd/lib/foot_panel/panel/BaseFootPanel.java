package com.sd.lib.foot_panel.panel;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public abstract class BaseFootPanel implements IFootPanel {
    private WeakReference<HeightChangeCallback> mHeightChangeCallback;

    /**
     * 通知高度
     */
    protected final void notifyHeight(int height) {
        final HeightChangeCallback callback = mHeightChangeCallback == null ? null : mHeightChangeCallback.get();
        if (callback != null) {
            callback.onHeightChanged(height, this);
        } else {
            releasePanel();
        }
    }

    @CallSuper
    @Override
    public void initPanel(@NonNull HeightChangeCallback callback) {
        mHeightChangeCallback = new WeakReference<>(callback);
    }

    @CallSuper
    @Override
    public void releasePanel() {
        mHeightChangeCallback = null;
    }
}
