package com.sd.lib.foot_panel.panel;

import com.sd.lib.foot_panel.FootPanelListener;

public interface IFootPanel
{
    /**
     * 返回面板当前的高度
     *
     * @return
     */
    int getPanelHeight();

    /**
     * 当面板被添加到{@link FootPanelListener}的时候触发
     *
     * @param callback
     */
    void initPanel(HeightChangeCallback callback);

    /**
     * 当面板从{@link FootPanelListener}移除的时候触发
     */
    void releasePanel();

    interface HeightChangeCallback
    {
        /**
         * 高度变化
         *
         * @param height
         */
        void onHeightChanged(int height);
    }
}
