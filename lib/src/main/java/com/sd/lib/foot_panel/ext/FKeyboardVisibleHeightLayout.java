package com.sd.lib.foot_panel.ext;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 高度为键盘可见高度的布局
 */
public class FKeyboardVisibleHeightLayout extends FKeyboardHeightLayout
{
    public FKeyboardVisibleHeightLayout(Context context)
    {
        super(context);
    }

    public FKeyboardVisibleHeightLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected int getKeyboardHeight()
    {
        int height = super.getKeyboardHeight();
        if (height == 0)
            height = FKeyboardListener.getCachedKeyboardVisibleHeight();
        return height;
    }
}
