package ru.phoenix.kidswatch.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ru.phoenix.kidswatch.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ScheduleItemView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var start = Date(0L)
    private var end = Date(0L)
    private val overlay = ContextCompat.getColor(context, R.color.overlay)
    private var bitmap: Bitmap? = null
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun setTime(timeStart: String, lengthMinutes: Int) {
        val minuteMs = 1000L * 60L
        start = timeFormat.parse(timeStart) ?: throw IllegalArgumentException("Parsing error.")
        end = Date(start.time + lengthMinutes * minuteMs)
        invalidate()
    }

    fun setIcon(resId: Int) {
        bitmap = BitmapFactory.decodeResource(context.resources, resId)
        bitmap = Bitmap.createScaledBitmap(bitmap!!, 100, 100, true)
        invalidate()
    }

    private val meshPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val overlayPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.overlay)
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        textSize = 36f
        typeface = Typeface.DEFAULT_BOLD
        style = Paint.Style.FILL
    }

    private val bitmapPaint = Paint()

    private fun drawHours(canvas: Canvas) {
        val minuteMs = 1000L * 60L
        val hourMs = 1000L * 60L * 60L
        val minuteSizePx = width.toFloat() / ((end.time - start.time) / minuteMs)
        for ((step, i) in (start.time until end.time step minuteMs).withIndex()) {
            if (i % hourMs == 0L) {
                val x = step * minuteSizePx + MESH_OFFSET_PX
                canvas.drawLine(x, 0f, x, height.toFloat(), meshPaint)
            }
        }
    }

    private fun drawOverlay(canvas: Canvas) {
        val current = Calendar.getInstance()
        val offset = (TimeZone.getDefault().rawOffset / (1000L * 60L * 60L)).toInt()
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, offset)
            set(Calendar.MINUTE, 0)
            timeInMillis += start.time
        }
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, offset)
            set(Calendar.MINUTE, 0)
            timeInMillis += end.time
        }
        when {
            current.timeInMillis >= endCalendar.timeInMillis -> canvas.drawColor(overlay)
            current.timeInMillis >= startCalendar.timeInMillis &&
                current.timeInMillis < endCalendar.timeInMillis -> {
                val rect = RectF(
                    0f,
                    0f,
                    width * (current.timeInMillis - startCalendar.timeInMillis).toFloat() / (endCalendar.timeInMillis - startCalendar.timeInMillis),
                    height.toFloat()
                )
                canvas.drawRect(rect, overlayPaint)
            }
        }
    }

    private fun drawIcon(canvas: Canvas) {
        bitmap?.let { canvas.drawBitmap(it, IMAGE_OFFSET_PX, IMAGE_OFFSET_PX, bitmapPaint) }
    }

    private fun drawTimeLabel(canvas: Canvas) {
        canvas.drawText(
            timeFormat.format(start),
            IMAGE_OFFSET_PX,
            (bitmap?.height ?: 0) + IMAGE_OFFSET_PX * 5,
            textPaint
        )
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            drawHours(this)
            drawOverlay(this)
            drawIcon(this)
            drawTimeLabel(this)
        }
    }

    init {
        setWillNotDraw(false)
    }

    companion object {

        const val MESH_OFFSET_PX = 5f
        const val IMAGE_OFFSET_PX = 10f

    }

}