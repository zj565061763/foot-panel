package com.example.foot_panel.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foot_panel.databinding.ViewInputBinding;
import com.sd.lib.foot_panel.FootPanelListener;
import com.sd.lib.foot_panel.ext.FKeyboardHeightKeeper;
import com.sd.lib.foot_panel.ext.FKeyboardVisibleHeightLayout;
import com.sd.lib.foot_panel.panel.IFootPanel;
import com.sd.lib.foot_panel.panel.KeyboardFootPanel;
import com.sd.lib.foot_panel.panel.ViewFootPanel;
import com.sd.lib.utils.FKeyboardUtil;

public class InputView extends FrameLayout implements View.OnClickListener {
    public static final String TAG = InputView.class.getSimpleName();

    private final ViewInputBinding mBinding;

    /** 键盘View */
    private View mKeyboardView;
    /** 更多View */
    private InputMoreView mMoreView;

    /** 键盘面板 */
    private final IFootPanel mKeyboardPanel;
    /** 底部扩展面板 */
    private final IFootPanel mBottomPanel;
    /** 键盘高度保持 */
    private FKeyboardHeightKeeper mKeyboardHeightKeeper;

    public InputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBinding = ViewInputBinding.inflate(LayoutInflater.from(context), this, true);

        // 创建面板
        mKeyboardPanel = new KeyboardFootPanel((Activity) context);
        mBottomPanel = new ViewFootPanel(mBinding.flBottomExtend);

        // 添加面板
        mFootPanelListener.addFootPanel(mKeyboardPanel);
        mFootPanelListener.addFootPanel(mBottomPanel);

        mBinding.etContent.setOnClickListener(this);
        mBinding.btnMore.setOnClickListener(this);
    }

    private FKeyboardHeightKeeper getKeyboardHeightKeeper() {
        if (mKeyboardHeightKeeper == null) {
            mKeyboardHeightKeeper = new FKeyboardHeightKeeper((Activity) getContext()) {
                @Override
                protected void updateViewHeight(View view, ViewGroup.LayoutParams params) {
                    Log.i(TAG, "updateViewHeight height:" + params.height + " view:" + view);
                    super.updateViewHeight(view, params);
                }
            };
        }
        return mKeyboardHeightKeeper;
    }

    private View getKeyboardView() {
        if (mKeyboardView == null) {
            mKeyboardView = new FKeyboardVisibleHeightLayout(getContext());
        }
        return mKeyboardView;
    }

    private InputMoreView getMoreView() {
        if (mMoreView == null) {
            mMoreView = new InputMoreView(getContext());
            // 同步键盘高度给View
            getKeyboardHeightKeeper().addView(mMoreView);
        }
        return mMoreView;
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.btnMore) {
            showModeMore();
        } else if (v == mBinding.etContent) {
            showModeKeyboard();
        }
    }

    /**
     * 显示更多模式
     */
    private void showModeMore() {
        mFootPanelListener.setCurrentFootPanel(mBottomPanel);

        mBinding.flBottomExtend.removeAllViews();
        mBinding.flBottomExtend.addView(getMoreView());
        FKeyboardUtil.hide(mBinding.etContent);
    }

    /**
     * 显示键盘输入模式
     */
    private void showModeKeyboard() {
        mBinding.flBottomExtend.removeAllViews();
        mBinding.flBottomExtend.addView(getKeyboardView());
        FKeyboardUtil.show(mBinding.etContent);
    }

    /**
     * 移除底部扩展
     */
    private void removeBottomExtend() {
        mFootPanelListener.setCurrentFootPanel(null);
        mBinding.flBottomExtend.removeAllViews();
    }

    private final FootPanelListener mFootPanelListener = new FootPanelListener() {
        @Override
        protected void onFootHeightChanged(int height) {
            Log.i(TAG, "onFootHeightChanged height:" + height);
            if (getCurrentFootPanel() == mKeyboardPanel) {
                if (height > 0) {
                    showModeKeyboard();
                } else {
                    removeBottomExtend();
                }
            }
        }

        @Override
        protected void onFootPanelChanged(@Nullable IFootPanel panel) {
            Log.i(TAG, "onFootPanelChanged panel:" + panel);
            super.onFootPanelChanged(panel);
        }
    };

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
}
