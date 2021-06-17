package com.example.foot_panel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foot_panel.databinding.ActivityScrollBinding

class ScrollActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityScrollBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScrollBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }
}