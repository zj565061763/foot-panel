package com.sd.lib.foot_panel.ext;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 将View的高度与键盘的高度保持一致
 */
public class FKeyboardHeightKeeper {
    private final Activity mActivity;
    private final Map<View, ViewConfig> mViewHolder = new WeakHashMap<>();

    private FKeyboardListener mKeyboardListener;
    private int mViewHeight = 0;
    private int mMinHeight = -1;

    public FKeyboardHeightKeeper(@NonNull Activity activity) {
        mActivity = activity;
    }

    private FKeyboardListener getKeyboardListener() {
        if (mKeyboardListener == null) {
            mKeyboardListener = FKeyboardListener.of(mActivity);
            mKeyboardListener.addCallback(mKeyboardCallback);
        }
        return mKeyboardListener;
    }

    private int getMinHeight() {
        if (mMinHeight < 0) {
            mMinHeight = dp2px(180, mActivity);
        }
        return mMinHeight;
    }

    /**
     * 设置最小高度
     * <p>
     * 如果键盘高度小于最小高度，则View的高度自动切换为{@link ViewGroup.LayoutParams#WRAP_CONTENT}
     */
    public void setMinHeight(int minHeight) {
        if (mMinHeight != minHeight) {
            mMinHeight = minHeight;
            notifyHeight(mViewHeight);
        }
    }

    /**
     * 添加View
     */
    public void addView(@NonNull View view) {
        if (mViewHolder.containsKey(view)) {
            return;
        }

        final ViewConfig config = new ViewConfig(view);
        mViewHolder.put(view, config);

        int height = getKeyboardListener().getKeyboardVisibleHeight();
        if (height <= 0) {
            height = FKeyboardListener.getCachedKeyboardVisibleHeight();
        }

        // 检查最小高度
        if (height < getMinHeight()) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        config.updateHeight(height);
    }

    /**
     * 移除View
     */
    public void removeView(View view) {
        if (view != null) {
            mViewHolder.remove(view);
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

    private void notifyHeight(int height) {
        if (height <= 0) {
            return;
        }

        // 检查最小高度
        if (height < getMinHeight()) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        if (mViewHeight != height) {
            mViewHeight = height;
            for (ViewConfig item : mViewHolder.values()) {
                item.updateHeight(height);
            }
        }
    }

    private final class ViewConfig {
        private final WeakReference<View> mView;

        private ViewConfig(View view) {
            if (view == null) {
                throw new NullPointerException("view is null");
            }
            mView = new WeakReference<>(view);
        }

        public void updateHeight(int height) {
            if (height == 0) {
                return;
            }

            final View view = mView.get();
            if (view == null) {
                return;
            }

            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                updateViewHeight(view, params);
                return;
            }

            if (params.height != height) {
                params.height = height;
                updateViewHeight(view, params);
            }
        }
    }

    protected void updateViewHeight(View view, ViewGroup.LayoutParams params) {
        view.setLayoutParams(params);
    }

    private static int dp2px(float dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
