package com.example.foot_panel.dialog;

import android.app.Activity;

import com.example.foot_panel.R;
import com.sd.lib.dialoger.impl.FDialoger;

public class TestDialog extends FDialoger
{
    public TestDialog(Activity activity)
    {
        super(activity);
        setPadding(0, 0, 0, 0);
        setContentView(R.layout.dialog_test);
    }
}
