package com.sd.demo.foot_panel.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sd.demo.foot_panel.databinding.ViewInputBinding;
import com.sd.lib.foot_panel.view.FootPanelLayout;
import com.sd.lib.utils.FKeyboardUtil;

public class InputView extends FrameLayout implements View.OnClickListener {
    private static final String TAG = InputView.class.getSimpleName();

    private final ViewInputBinding mBinding;

    /** 更多View */
    private InputMoreView mMoreView;

    public InputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBinding = ViewInputBinding.inflate(LayoutInflater.from(context), this, true);

        mBinding.btnMore.setOnClickListener(this);
        mBinding.footPanelLayout.setCallback(new FootPanelLayout.Callback() {
            @Override
            public void onFootHeightChanged(int height) {
                Log.i(TAG, "onFootHeightChanged height:" + height);
            }
        });
    }

    private InputMoreView getMoreView() {
        if (mMoreView == null) {
            mMoreView = new InputMoreView(getContext());
        }
        return mMoreView;
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.btnMore) {
            // 点击更多
            mBinding.footPanelLayout.setContentView(getMoreView());
            FKeyboardUtil.hide(mBinding.etContent);
        }
    }
}
