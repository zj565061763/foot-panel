package com.sd.demo.foot_panel

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.foot_panel.databinding.ActivityWindowBinding
import com.sd.lib.foot_panel.ext.FWindowKeyboardListener

/**
 * 软键盘监听
 */
class WindowActivity : AppCompatActivity() {
    private val TAG = WindowActivity::class.java.simpleName
    private lateinit var _binding: ActivityWindowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityWindowBinding.inflate(layoutInflater)
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
            _binding.etContent.setText(height.toString())
        }
    }
}