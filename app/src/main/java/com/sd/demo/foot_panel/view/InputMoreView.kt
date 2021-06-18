package com.sd.demo.foot_panel.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sd.demo.foot_panel.R

/**
 * 更多View
 */
class InputMoreView : FrameLayout {
    constructor(context: Context) : super(context) {
        LayoutInflater.from(context).inflate(R.layout.view_input_more, this, true)
    }
}