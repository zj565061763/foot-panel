package com.example.foot_panel.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.foot_panel.view.InputView;
import com.sd.lib.dialoger.impl.FDialoger;
import com.sd.lib.utils.context.FResUtil;

public class TestDialog extends FDialoger
{
    private final InputView mInputView;
    private final int mDefaultHeight = (int) (FResUtil.getScreenHeight() * 0.5f);

    public TestDialog(Activity activity)
    {
        super(activity);
        setCanceledOnTouchOutside(false);
        setPadding(0, 0, 0, 0);
        setBackgroundDim(false);
        setGravity(Gravity.BOTTOM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        mInputView = new InputView(activity, null);
        mInputView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mDefaultHeight));
        setContentView(mInputView);
    }
}
