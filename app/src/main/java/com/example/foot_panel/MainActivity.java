package com.example.foot_panel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foot_panel.databinding.ActivityMainBinding;
import com.example.foot_panel.dialog.TestDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.btnKeyboard.setOnClickListener(this);
        mBinding.btnFootPanel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == mBinding.btnKeyboard)
        {
            startActivity(new Intent(this, KeyboardActivity.class));
        } else if (v == mBinding.btnFootPanel)
        {
            startActivity(new Intent(this, FootPanelActivity.class));
        }
    }
}