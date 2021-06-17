package com.sd.lib.foot_panel.panel;

import androidx.annotation.NonNull;

import com.sd.lib.foot_panel.FootPanelListener;

public interface IFootPanel {
    /**
     * 返回面板当前的高度
     */
    int getPanelHeight();

    /**
     * 当面板被添加到{@link FootPanelListener}的时候触发
     */
    void initPanel(@NonNull HeightChangeCallback callback);

    /**
     * 当面板从{@link FootPanelListener}移除的时候触发
     */
    void releasePanel();

    interface HeightChangeCallback {
        /**
         * 高度变化
         */
        void onHeightChanged(int height, @NonNull IFootPanel footPanel);
    }
}
