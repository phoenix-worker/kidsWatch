package ru.phoenix.kidswatch.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View

class AnalogWatchView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawColor(Color.parseColor("#ffffff"))
    }

}