package com.sd.demo.foot_panel

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.foot_panel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }

    override fun onClick(v: View) {
        when (v) {
            _binding.btnWindow -> startActivity(Intent(this, WindowActivity::class.java))
            _binding.btnKeyboard -> startActivity(Intent(this, KeyboardActivity::class.java))
            _binding.btnFootPanel -> startActivity(Intent(this, FootPanelActivity::class.java))
            _binding.btnScroll -> startActivity(Intent(this, ScrollActivity::class.java))
        }
    }
}