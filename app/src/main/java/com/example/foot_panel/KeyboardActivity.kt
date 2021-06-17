package com.example.foot_panel

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.foot_panel.databinding.ActivityKeyboardBinding
import com.example.foot_panel.dialog.TestDialog
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

        // 由于[FKeyboardListener]内部采用弱引用保存回调对象，所以这边回调对象要强引用
        FKeyboardListener.of(this).addCallback(_callback)
        Log.i(TAG, "getCachedKeyboardVisibleHeight:" + FKeyboardListener.getCachedKeyboardVisibleHeight())
    }

    private val _callback = FKeyboardListener.Callback { height, listener ->
        Log.i(TAG, "onKeyboardHeightChanged height:${height}")
    }

    override fun onClick(v: View?) {
        when (v) {
            _binding.btn -> TestDialog(this@KeyboardActivity).show()
        }
    }
}