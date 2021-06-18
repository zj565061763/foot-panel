package com.sd.lib.foot_panel.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sd.lib.foot_panel.FootPanelListener;
import com.sd.lib.foot_panel.ext.FKeyboardHeightKeeper;
import com.sd.lib.foot_panel.ext.FKeyboardHeightLayout;
import com.sd.lib.foot_panel.panel.IFootPanel;
import com.sd.lib.foot_panel.panel.KeyboardFootPanel;
import com.sd.lib.foot_panel.panel.ViewFootPanel;

public class FootPanelLayout extends FrameLayout {
    private final Activity mActivity;

    private final IFootPanel mKeyboardPanel;
    private final IFootPanel mViewPanel;

    /** 内容View */
    private View mContentView;

    /** 键盘高度View */
    private View mKeyBoardView;
    /** 键盘高度保持 */
    private FKeyboardHeightKeeper mKeyboardHeightKeeper;

    private Callback mCallback;

    public FootPanelLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            throw new IllegalArgumentException("context must be instance of " + Activity.class.getName());
        }

        mKeyboardPanel = new KeyboardFootPanel(mActivity);
        mViewPanel = new ViewFootPanel(this);

        mFootPanelListener.addFootPanel(mKeyboardPanel);
        mFootPanelListener.addFootPanel(mViewPanel);
    }

    /**
     * 设置回调对象
     */
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    /**
     * 设置内容View
     */
    public final void setContentView(View view) {
        final View oldView = mContentView;
        if (oldView == view) {
            return;
        }

        mContentView = view;

        if (oldView != null) {
            removeView(oldView);
            getKeyboardHeightKeeper().removeView(oldView);
        }

        if (view != null) {
            if (view.getParent() != FootPanelLayout.this) {
                removeViewFromParent(view);
            }
            addView(view);

            if (view != mKeyBoardView) {
                mFootPanelListener.setCurrentFootPanel(mViewPanel);
                getKeyboardHeightKeeper().addView(view);
            }
        } else {
            mFootPanelListener.setCurrentFootPanel(null);
        }
    }

    private final FootPanelListener mFootPanelListener = new FootPanelListener() {
        @Override
        protected void onFootHeightChanged(int height) {
            if (mCallback != null) {
                mCallback.onFootHeightChanged(height);
            }
        }

        @Override
        protected void onFootPanelChanged(@Nullable IFootPanel panel) {
            super.onFootPanelChanged(panel);
            if (panel == mKeyboardPanel) {
                setContentView(getKeyBoardView());
            }
        }
    };

    private View getKeyBoardView() {
        if (mKeyBoardView == null) {
            mKeyBoardView = new FKeyboardHeightLayout(mActivity);
        }
        return mKeyBoardView;
    }

    private FKeyboardHeightKeeper getKeyboardHeightKeeper() {
        if (mKeyboardHeightKeeper == null) {
            mKeyboardHeightKeeper = new FKeyboardHeightKeeper(mActivity);
        }
        return mKeyboardHeightKeeper;
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child != mContentView) {
            throw new RuntimeException("Illegal child:" + child);
        }
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        if (child == mContentView) {
            setContentView(null);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFootPanelListener.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFootPanelListener.stop();
    }

    private static void removeViewFromParent(View view) {
        if (view == null) {
            return;
        }

        final ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            try {
                ((ViewGroup) parent).removeView(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface Callback {
        void onFootHeightChanged(int height);
    }
}