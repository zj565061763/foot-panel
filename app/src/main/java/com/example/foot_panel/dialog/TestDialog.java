package com.example.foot_panel.dialog;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.example.foot_panel.R;
import com.sd.lib.dialoger.impl.FDialoger;
import com.sd.lib.foot_panel.ext.FWindowKeyboardListener;

public class TestDialog extends FDialoger
{
    private static final String TAG = TestDialog.class.getSimpleName();

    private final FWindowKeyboardListener mKeyboardListener;

    public TestDialog(Activity activity)
    {
        super(activity);
        setCanceledOnTouchOutside(false);
        setPadding(0, 0, 0, 0);
        setGravity(Gravity.BOTTOM);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.dialog_test);

        mKeyboardListener = new FWindowKeyboardListener(activity)
        {
            @Override
            protected void onKeyboardHeightChanged(int height)
            {
                Log.i(TAG, "onKeyboardHeightChanged:" + height);
            }

            @Override
            protected void onStart()
            {
                super.onStart();
                Log.i(TAG, "onStart");
            }

            @Override
            protected void onStop()
            {
                super.onStop();
                Log.i(TAG, "onStop");
            }
        };
        mKeyboardListener.start(getWindow());
    }
}
