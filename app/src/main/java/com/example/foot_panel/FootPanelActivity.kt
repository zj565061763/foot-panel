package com.example.foot_panel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foot_panel.databinding.ActivityFootPanelBinding

class FootPanelActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityFootPanelBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFootPanelBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }
}