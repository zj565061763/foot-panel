package com.sd.lib.foot_panel.ext;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 将View的高度与键盘的高度保持一致
 */
public class FKeyboardHeightKeeper {
    private final Activity mActivity;
    private final int mMinHeight;
    private final Map<View, ViewConfig> mViewHolder = new WeakHashMap<>();

    private FKeyboardListener mKeyboardListener;
    private int mViewHeight = 0;

    public FKeyboardHeightKeeper(@NonNull Activity activity) {
        this(activity, -1);
    }

    public FKeyboardHeightKeeper(@NonNull Activity activity, int minHeight) {
        mActivity = activity;
        mMinHeight = minHeight < 0 ? dp2px(180, mActivity) : minHeight;
    }

    private FKeyboardListener getKeyboardListener() {
        if (mKeyboardListener == null) {
            mKeyboardListener = FKeyboardListener.of(mActivity);
            mKeyboardListener.addCallback(mKeyboardCallback);
        }
        return mKeyboardListener;
    }

    /**
     * 添加View
     */
    public final void addView(@NonNull View view) {
        if (mViewHolder.containsKey(view)) {
            return;
        }

        final ViewConfig config = new ViewConfig(view);
        mViewHolder.put(view, config);

        int height = getKeyboardListener().getKeyboardVisibleHeight();
        if (height <= 0) {
            height = FKeyboardListener.getCachedKeyboardVisibleHeight();
        }

        config.updateHeight(height);
    }

    /**
     * 移除View
     */
    public final void removeView(View view) {
        if (view != null) {
            mViewHolder.remove(view);
        }
    }

    /**
     * 监听软键盘
     */
    private final FKeyboardListener.Callback mKeyboardCallback = new FKeyboardListener.Callback() {
        @Override
        public void onKeyboardHeightChanged(int height, @NonNull FKeyboardListener listener) {
            notifyHeight(height);
        }
    };

    private void notifyHeight(int height) {
        if (height <= 0) {
            return;
        }

        if (mViewHeight == height) {
            return;
        }

        mViewHeight = height;
        final List<ViewConfig> list = new ArrayList<>(mViewHolder.values());
        for (ViewConfig item : list) {
            item.updateHeight(height);
        }
    }

    private final class ViewConfig {
        private final WeakReference<View> mView;

        private ViewConfig(@NonNull View view) {
            mView = new WeakReference<>(view);
        }

        public void updateHeight(int height) {
            final View view = mView.get();
            if (view == null) {
                return;
            }

            if (height <= 0) {
                return;
            }

            if (height < mMinHeight) {
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            final ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params == null) {
                updateViewHeight(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
                return;
            }

            if (params.height != height) {
                params.height = height;
                updateViewHeight(view, params);
            }
        }
    }

    /**
     * 更新View的高度
     */
    protected void updateViewHeight(View view, ViewGroup.LayoutParams params) {
        view.setLayoutParams(params);
    }

    private static int dp2px(float dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
