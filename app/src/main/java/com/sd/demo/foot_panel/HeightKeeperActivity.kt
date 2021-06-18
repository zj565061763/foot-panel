package com.sd.demo.foot_panel

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.foot_panel.ext.FKeyboardHeightKeeper

class HeightKeeperActivity : AppCompatActivity() {
    private val TAG = HeightKeeperActivity::class.java.simpleName

    /** 键盘高度保持 */
    private val _keyboardHeightKeeper = object : FKeyboardHeightKeeper(this) {
        override fun updateViewHeight(view: View, params: ViewGroup.LayoutParams) {
            super.updateViewHeight(view, params)
            Log.i(TAG, "updateViewHeight height:${params.height} view:${view}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_height_keeper)

        // 添加要保持键盘高度的View
        _keyboardHeightKeeper.addView(findViewById(R.id.tv_content))
    }
}