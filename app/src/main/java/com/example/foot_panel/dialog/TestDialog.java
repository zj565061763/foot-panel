package com.example.foot_panel.dialog;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.example.foot_panel.R;
import com.sd.lib.dialoger.impl.FDialoger;
import com.sd.lib.foot_panel.ext.FKeyboardListener;

public class TestDialog extends FDialoger
{
    private static final String TAG = TestDialog.class.getSimpleName();

    public TestDialog(Activity activity)
    {
        super(activity);
        setCanceledOnTouchOutside(false);
        setPadding(0, 0, 0, 0);
        setBackgroundDim(false);
        setGravity(Gravity.BOTTOM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        setContentView(R.layout.dialog_test);
    }

    private final FKeyboardListener.Callback mKeyboardCallback = new FKeyboardListener.Callback()
    {
        @Override
        public void onKeyboardHeightChanged(int height, FKeyboardListener listener)
        {
            Log.i(TAG, "onKeyboardHeightChanged:" + height);
        }
    };

    @Override
    protected void onStart()
    {
        super.onStart();
        FKeyboardListener.of(getOwnerActivity()).addCallback(mKeyboardCallback);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FKeyboardListener.of(getOwnerActivity()).removeCallback(mKeyboardCallback);
    }
}
