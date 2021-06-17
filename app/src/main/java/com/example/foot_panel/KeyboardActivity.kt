package com.example.foot_panel

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.foot_panel.databinding.ActivityKeyboardBinding
import com.example.foot_panel.dialog.TestDialog
import com.sd.lib.foot_panel.ext.FWindowKeyboardListener

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

        // 监听当前window
        _windowKeyboardListener.start(window)
    }

    /**
     * 监听键盘高度
     */
    private val _windowKeyboardListener = object : FWindowKeyboardListener() {
        override fun onStart() {
            super.onStart()
            Log.i(TAG, "listener onStart")
        }

        override fun onStop() {
            super.onStop()
            Log.i(TAG, "listener onStop")
        }

        override fun onKeyboardHeightChanged(height: Int) {
            Log.i(TAG, "listener onKeyboardHeightChanged height:${height}")
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            _binding.btn -> TestDialog(this@KeyboardActivity).show()
        }
    }
}