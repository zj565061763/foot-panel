package com.sd.demo.foot_panel.dialog;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.sd.demo.foot_panel.R;
import com.sd.lib.dialoger.impl.FDialoger;
import com.sd.lib.foot_panel.ext.FKeyboardListener;

public class TestDialog extends FDialoger {
    private static final String TAG = TestDialog.class.getSimpleName();

    private View view_root;

    public TestDialog(final Activity activity) {
        super(activity);
        setCanceledOnTouchOutside(false);
        setPadding(0, 0, 0, 0);
        setGravity(Gravity.BOTTOM);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.dialog_test);
        view_root = findViewById(R.id.view_root);

        // 监听当前Window的键盘
        FKeyboardListener.of(activity).checkWindow(getWindow());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FKeyboardListener.of(getOwnerActivity()).addCallback(mKeyboardCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FKeyboardListener.of(getOwnerActivity()).addCallback(mKeyboardCallback);
    }

    private final FKeyboardListener.Callback mKeyboardCallback = new FKeyboardListener.Callback() {
        @Override
        public void onKeyboardHeightChanged(int height, FKeyboardListener listener) {
            Log.i(TAG, "onKeyboardHeightChanged:" + height);
            view_root.scrollTo(0, height);
        }
    };
}
