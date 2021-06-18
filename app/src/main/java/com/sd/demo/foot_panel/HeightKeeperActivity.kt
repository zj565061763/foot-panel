package com.sd.demo.foot_panel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.foot_panel.ext.FKeyboardHeightKeeper

class HeightKeeperActivity : AppCompatActivity() {
    private val _keyboardHeightKeeper = FKeyboardHeightKeeper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_height_keeper)

        // 添加要保持键盘高度的View
        _keyboardHeightKeeper.addView(findViewById(R.id.tv_content))
    }
}