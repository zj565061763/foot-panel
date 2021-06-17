package com.sd.demo.foot_panel

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.foot_panel.databinding.ActivityKeyboardBinding
import com.sd.demo.foot_panel.dialog.TestDialog
import com.sd.lib.foot_panel.ext.FKeyboardListener

/**
 * 软键盘监听
 */
class KeyboardActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = KeyboardActivity::class.java.simpleName
    private lateinit var _binding: ActivityKeyboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityKeyboardBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        // 监听当前Activity
        FKeyboardListener.of(this).addCallback(_keyboardCallback)
    }

    /**
     * 键盘高度回调
     */
    private val _keyboardCallback = object : FKeyboardListener.Callback {
        override fun onKeyboardHeightChanged(height: Int, listener: FKeyboardListener) {
            Log.i(TAG, "onKeyboardHeightChanged height:${height}")
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            _binding.btn -> TestDialog(this@KeyboardActivity).show()
        }
    }
}