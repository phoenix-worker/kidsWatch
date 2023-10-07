package ru.phoenix.kidswatch.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import ru.phoenix.kidswatch.R
import ru.phoenix.kidswatch.fragments.SettingsFragment.Companion.PREF_TIME_FORMAT
import ru.phoenix.kidswatch.fragments.SettingsFragment.Companion.TIME_FORMAT_12
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

class AnalogWatchView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs), Handler.Callback {

    private var watchHandler: Handler? = null
    private val handlerCalendar = Calendar.getInstance()
    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    init {
        watchHandler = Handler(Looper.getMainLooper(), this)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.let {
            drawHours(it)
            drawHoursDigits(it)
            drawMinutesDigits(it)
            drawMinutes(it)
            drawCircle(it)
            drawHourHandle(it)
            drawMinuteHandle(it)
            drawSecondHandle(it)
            drawCenterCirce(it)
        }
    }

    private val centerCirclePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(
            10f,
            2f,
            2f,
            ContextCompat.getColor(context, R.color.black)
        )
    }

    private fun drawCenterCirce(canvas: Canvas) {
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            width / 2f * centerCircleRatio,
            centerCirclePaint
        )
    }

    private val secondHandlePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.secondHandle)
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
        setShadowLayer(
            10f,
            2f,
            2f,
            ContextCompat.getColor(context, R.color.black)
        )
    }

    private fun drawSecondHandle(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        val angle = 2 * Math.PI * calendar.get(Calendar.SECOND) / 60f
        val length = secondHandleRatio * width / 2
        val x = length * sin(angle)
        val y = length * cos(angle)
        canvas.drawLine(
            width / 2f,
            height / 2f,
            width / 2 + x.toFloat(),
            height / 2 - y.toFloat(),
            secondHandlePaint
        )
    }

    private val minuteHandlePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 15f
        isAntiAlias = true
        setShadowLayer(
            10f,
            2f,
            2f,
            ContextCompat.getColor(context, R.color.black)
        )
    }

    private fun drawMinuteHandle(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        val angle = 2 * Math.PI * calendar.get(Calendar.MINUTE) / 60f
        val length = minuteHandleRatio * width / 2
        val x = length * sin(angle)
        val y = length * cos(angle)
        canvas.drawLine(
            width / 2f,
            height / 2f,
            width / 2 + x.toFloat(),
            height / 2 - y.toFloat(),
            minuteHandlePaint
        )
    }

    private val hourHandlePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.analogWatchesHours)
        style = Paint.Style.STROKE
        strokeWidth = 25f
        isAntiAlias = true
    }

    private fun drawHourHandle(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        var angle = 2 * Math.PI * calendar.get(Calendar.HOUR) / 12f
        angle += (2 * Math.PI / 12f) * (calendar.get(Calendar.MINUTE) / 60f)
        val length = hourHandleRatio * width / 2
        val x = length * sin(angle)
        val y = length * cos(angle)
        canvas.drawLine(
            width / 2f,
            height / 2f,
            width / 2 + x.toFloat(),
            height / 2 - y.toFloat(),
            hourHandlePaint
        )
    }

    private val minutesPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private fun drawMinutes(canvas: Canvas) {
        for (i in 0..59) {
            if (i % 5 == 0) continue
            val angle = 2 * Math.PI * (i / 60f)
            val outerR = circleRadiusRatio * width / 2
            val innerR = minutesRadiusRatio * width / 2
            val outerX = outerR * sin(angle)
            val outerY = outerR * cos(angle)
            val innerX = innerR * sin(angle)
            val innerY = innerR * cos(angle)
            canvas.drawLine(
                width / 2 + outerX.toFloat(),
                height / 2 - outerY.toFloat(),
                width / 2 + innerX.toFloat(),
                height / 2 - innerY.toFloat(),
                minutesPaint
            )
        }
    }

    private val hoursPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.analogWatchesHours)
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }

    private fun drawHours(canvas: Canvas) {
        for (i in 0..11) {
            val angle = 2 * Math.PI * (i / 12f)
            val outerR = circleRadiusRatio * width / 2
            val innerR = hoursRadiusRatio * width / 2
            val outerX = outerR * sin(angle)
            val outerY = outerR * cos(angle)
            val innerX = innerR * sin(angle)
            val innerY = innerR * cos(angle)
            canvas.drawLine(
                width / 2 + outerX.toFloat(),
                height / 2 - outerY.toFloat(),
                width / 2 + innerX.toFloat(),
                height / 2 - innerY.toFloat(),
                hoursPaint
            )
        }
    }

    private val hoursDigitsPaint = Paint().apply {
        style = Paint.Style.FILL
        typeface = Typeface.DEFAULT_BOLD
        color = ContextCompat.getColor(context, R.color.analogWatchesHours)
        textAlign = Paint.Align.CENTER
    }

    private fun drawHoursDigits(canvas: Canvas) {
        hoursDigitsPaint.textSize = digitsRatio * width
        for (i in 0..11) {
            val angle = 2 * Math.PI * (i / 12f)
            val outerR = hoursDigitsRatio * width / 2
            val outerX = outerR * sin(angle)
            val outerY = outerR * cos(angle)
            val hourOfDay = handlerCalendar.get(Calendar.HOUR_OF_DAY)
            val text = when {
                hourOfDay < 12 -> getHoursTextFor12HourFormat(i)

                else -> {
                    when (prefs.getInt(PREF_TIME_FORMAT, TIME_FORMAT_12)) {
                        TIME_FORMAT_12 -> getHoursTextFor12HourFormat(i)
                        else -> getHoursTextFor24HourFormat(i)
                    }
                }
            }
            val rect = Rect()
            hoursDigitsPaint.getTextBounds(text, 0, text.length, rect)
            canvas.drawText(
                text,
                width / 2 + outerX.toFloat(),
                height / 2 - outerY.toFloat() + rect.height() / 2,
                hoursDigitsPaint
            )
        }
    }

    private fun getHoursTextFor12HourFormat(i: Int): String {
        return if (i != 0) i.toString() else "12"
    }

    private fun getHoursTextFor24HourFormat(i: Int): String {
        return if (i != 0) (i + 12).toString() else "0"
    }

    private val minutesDigitsPaint = Paint().apply {
        style = Paint.Style.FILL
        typeface = Typeface.DEFAULT_BOLD
        color = ContextCompat.getColor(context, R.color.white)
        textAlign = Paint.Align.CENTER
    }

    private fun drawMinutesDigits(canvas: Canvas) {
        minutesDigitsPaint.textSize = digitsRatio * width
        for (i in 0..59 step 5) {
            val angle = 2 * Math.PI * (i / 60f)
            val outerR = minutesDigitsRatio * width / 2
            val outerX = outerR * sin(angle)
            val outerY = outerR * cos(angle)
            val text = if (i != 60) i.toString() else "0"
            val rect = Rect()
            minutesDigitsPaint.getTextBounds(text, 0, text.length, rect)
            canvas.drawText(
                text,
                width / 2 + outerX.toFloat(),
                height / 2 - outerY.toFloat() + rect.height() / 2,
                minutesDigitsPaint
            )
        }
    }

    private val circlePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            circleRadiusRatio * width / 2,
            circlePaint
        )
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            ScheduleView.MSG_UPDATE_WATCHES -> {
                invalidate()
                watchHandler?.sendEmptyMessageDelayed(
                    ScheduleView.MSG_UPDATE_WATCHES,
                    getHandlerMsgDelay()
                )
            }
        }
        return true
    }

    private fun getHandlerMsgDelay(): Long {
        handlerCalendar.timeInMillis = System.currentTimeMillis()
        return ScheduleView.UPDATE_INTERVAL - handlerCalendar.get(Calendar.MILLISECOND)
    }

    fun startWatches() {
        watchHandler?.sendEmptyMessageDelayed(ScheduleView.MSG_UPDATE_WATCHES, getHandlerMsgDelay())
    }

    fun stopWatches() {
        watchHandler?.removeMessages(ScheduleView.MSG_UPDATE_WATCHES)
    }

    private val circleRadiusRatio = 0.8f
    private val hoursRadiusRatio = 0.7f
    private val hoursDigitsRatio = 0.62f
    private val minutesDigitsRatio = 0.9f
    private val minutesRadiusRatio = 0.75f
    private val hourHandleRatio = 0.5f
    private val minuteHandleRatio = 0.65f
    private val secondHandleRatio = 0.68f
    private val centerCircleRatio = 0.07f
    private val digitsRatio = 0.05f

}