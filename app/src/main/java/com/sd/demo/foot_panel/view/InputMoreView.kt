package com.sd.demo.foot_panel.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.sd.demo.foot_panel.databinding.ViewInputMoreBinding;

public class InputMoreView extends FrameLayout {
    private final ViewInputMoreBinding mBinding;

    public InputMoreView(Context context) {
        super(context);
        mBinding = ViewInputMoreBinding.inflate(LayoutInflater.from(context), this, true);
    }
}
