package com.example.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class MyRadioButton(context: Context, attributeSet: AttributeSet): AppCompatRadioButton(context, attributeSet) {

    init {
        //call function to apply font to components
        applyFont()
    }
    private fun applyFont() {
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "sf_pro_display.OTF")
        setTypeface(typeface)
    }
}