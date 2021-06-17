package com.sd.lib.foot_panel.panel;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public abstract class BaseFootPanel implements IFootPanel {
    private WeakReference<HeightChangeCallback> mHeightChangeCallback;

    protected final HeightChangeCallback getHeightChangeCallback() {
        return mHeightChangeCallback == null ? null : mHeightChangeCallback.get();
    }

    @Override
    public void initPanel(@NonNull HeightChangeCallback callback) {
        mHeightChangeCallback = new WeakReference<>(callback);
    }

    @Override
    public void releasePanel() {
        mHeightChangeCallback = null;
    }
}
