package com.sd.lib.foot_panel;

import androidx.annotation.NonNull;

import com.sd.lib.foot_panel.panel.IFootPanel;
import com.sd.lib.foot_panel.panel.KeyboardFootPanel;

import java.util.HashMap;
import java.util.Map;

public abstract class FootPanelListener {
    private final Map<IFootPanel, IFootPanel.HeightChangeCallback> mMapFootPanel = new HashMap<>();

    private IFootPanel mCurrentFootPanel;
    private KeyboardFootPanel mKeyboardFootPanel;

    private boolean mIsStarted;
    private int mFootHeight;

    /**
     * 是否已经开始监听
     */
    public final boolean isStarted() {
        return mIsStarted;
    }

    /**
     * 返回当前底部高度
     */
    public final int getFootHeight() {
        return mFootHeight;
    }

    /**
     * 返回当前底部面板
     */
    public final IFootPanel getCurrentFootPanel() {
        return mCurrentFootPanel;
    }

    /**
     * 开始监听
     */
    public final void start() {
        if (!mIsStarted) {
            mIsStarted = true;
            notifyHeightChanged();
        }
    }

    /**
     * 停止监听
     */
    public final void stop() {
        mIsStarted = false;
    }

    /**
     * 添加底部面板
     */
    public final void addFootPanel(@NonNull IFootPanel panel) {
        if (mMapFootPanel.containsKey(panel)) {
            return;
        }

        final IFootPanel.HeightChangeCallback callback = new IFootPanel.HeightChangeCallback() {
            @Override
            public void onHeightChanged(int height, @NonNull IFootPanel footPanel) {
                if (footPanel == mKeyboardFootPanel) {
                    if (height > 0) {
                        // 如果软键盘弹出，则自动设置当前面板为软键盘面板
                        setCurrentFootPanel(mKeyboardFootPanel);
                    }
                } else if (footPanel == mCurrentFootPanel) {
                    setFootHeight(height);
                }
            }
        };

        mMapFootPanel.put(panel, callback);
        panel.initPanel(callback);

        if (panel instanceof KeyboardFootPanel) {
            if (mKeyboardFootPanel == null) {
                mKeyboardFootPanel = (KeyboardFootPanel) panel;
            } else {
                throw new IllegalArgumentException(KeyboardFootPanel.class.getSimpleName() + " already add");
            }
        }
    }

    /**
     * 移除底部面板
     */
    public void removeFootPanel(final IFootPanel panel) {
        final IFootPanel.HeightChangeCallback callback = mMapFootPanel.remove(panel);
        if (callback != null) {
            if (mKeyboardFootPanel == panel) {
                mKeyboardFootPanel = null;
            }

            panel.releasePanel();

            if (mCurrentFootPanel == panel) {
                setCurrentFootPanel(null);
            }
        }
    }

    /**
     * 设置当前底部面板
     */
    public void setCurrentFootPanel(IFootPanel panel) {
        if (panel == null) {
            mCurrentFootPanel = null;
            setFootHeight(0);
            return;
        }

        if (mMapFootPanel.containsKey(panel)) {
            mCurrentFootPanel = panel;

            final int height = panel.getPanelHeight();
            if (height > 0) {
                setFootHeight(height);
            }
        }
    }

    /**
     * 设置底部高度
     */
    private void setFootHeight(int height) {
        if (height < 0) {
            height = 0;
        }

        if (mFootHeight != height) {
            mFootHeight = height;
            notifyHeightChanged();
        }
    }

    /**
     * 通知底部高度变化
     */
    private void notifyHeightChanged() {
        if (mIsStarted) {
            onFootHeightChanged(mFootHeight);
        }
    }

    /**
     * 底部高度变化
     */
    protected abstract void onFootHeightChanged(int height);
}
