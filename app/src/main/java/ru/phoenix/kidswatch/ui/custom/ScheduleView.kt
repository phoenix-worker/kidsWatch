package ru.phoenix.kidswatch.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import ru.phoenix.kidswatch.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ScheduleView(context: Context, attrs: AttributeSet) : View(context, attrs), Handler.Callback {

    private var watchHandler: Handler? = null
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val rows = mutableListOf<Row>()
    private var faceBitmap: Bitmap? = null
    private val events = mutableListOf<Event>()

    fun initialize(rows: List<Row.RowInitializer>) {
        watchHandler = Handler(Looper.getMainLooper(), this)
        initFaceImage()
        doOnLayout {
            createRectFs(rows)
            invalidate()
        }
    }

    private val eventImageSize = 100
    fun addEvent(time: Long, iconRes: Int) {
        var bitmap = BitmapFactory.decodeResource(context.resources, iconRes)
        bitmap = Bitmap.createScaledBitmap(bitmap, eventImageSize, eventImageSize, true)
        events.add(Event(time, bitmap))
    }

    private val faceImageSize = 150
    private fun initFaceImage() {
        faceBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.image_fedor)
        faceBitmap = Bitmap.createScaledBitmap(faceBitmap!!, faceImageSize, faceImageSize, true)
    }

    private val rectMargin = 10f
    private fun createRectFs(schedule: List<Row.RowInitializer>) {
        this.rows.clear()
        val rowHeight = height.toFloat() / schedule.size
        for ((index, initializer) in schedule.withIndex()) {
            this.rows.add(
                Row(
                    start = initializer.start,
                    end = initializer.end,
                    rectF = RectF(
                        rowStrokeWidth / 2,
                        index * rowHeight + rowStrokeWidth / 2 + rectMargin / 2,
                        width.toFloat() - rowStrokeWidth / 2,
                        (index + 1) * rowHeight - rowStrokeWidth / 2 - rectMargin / 2
                    ),
                    color = initializer.color
                )
            )
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawRows(canvas)
            drawHours(canvas)
            drawOverlay(canvas)
            drawEvents(canvas)
        }
    }

    private val rowStrokeWidth = 8f
    private val rowPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        strokeWidth = rowStrokeWidth
        style = Paint.Style.STROKE
    }

    private fun drawRows(canvas: Canvas) {
        for (row in rows) {
            rowPaint.color = row.color
            rowPaint.style = Paint.Style.FILL
            canvas.drawRect(row.rectF, rowPaint)
            rowPaint.color = ContextCompat.getColor(context, R.color.white)
            rowPaint.style = Paint.Style.STROKE
            canvas.drawRect(row.rectF, rowPaint)
        }
    }

    private val meshPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        textSize = 36f
        typeface = Typeface.DEFAULT_BOLD
        style = Paint.Style.FILL
    }

    private val textOffsetX = 15f
    private val textOffsetY = 50f
    private fun drawHours(canvas: Canvas) {
        rows.forEach { row ->
            val minuteMs = 1000L * 60L
            val hourMs = 1000L * 60L * 60L
            val minuteSizePx = width.toFloat() / ((row.end - row.start) / minuteMs)
            for ((step, i) in (row.start until row.end step minuteMs).withIndex()) {
                if (i % hourMs == 0L) {
                    val x = step * minuteSizePx
                    canvas.drawLine(x, row.rectF.top, x, row.rectF.bottom, meshPaint)
                    canvas.drawText(
                        timeFormat.format(Date(i)),
                        x + textOffsetX,
                        row.rectF.top + textOffsetY,
                        textPaint
                    )
                }
            }
        }
    }

    private val safeZonePx = 100
    private val verticalOffsetFactor = 0.3f
    private fun drawOverlay(canvas: Canvas) {
        rows.forEach { row ->
            val current = Calendar.getInstance()
            val currentY = row.rectF.centerY() + row.rectF.height() * verticalOffsetFactor
            when {
                current.timeInMillis >= row.start && current.timeInMillis < row.end -> {
                    var currentX =
                        row.rectF.width() * (current.timeInMillis - row.start).toFloat() / (row.end - row.start)
                    when {
                        currentX + safeZonePx > row.rectF.right -> currentX =
                            row.rectF.right - safeZonePx
                        currentX - safeZonePx < row.rectF.left -> currentX =
                            row.rectF.left + safeZonePx
                    }
                    drawWatches(canvas, currentX, currentY)
                    drawFace(canvas, currentX, currentY)
                }
            }
        }
    }

    private val watchPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.watchTextColor)
        textAlign = Paint.Align.CENTER
        textSize = 30f
        typeface = Typeface.DEFAULT_BOLD
    }
    private val watchBackgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
    }
    private val watchBackMargin = 15f
    private fun drawWatches(canvas: Canvas, x: Float, y: Float) {
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val text = format.format(Date())
        val rect = Rect()
        watchPaint.getTextBounds(text, 0, text.length, rect)
        val backRect = RectF(
            x - rect.width() / 2f - watchBackMargin,
            y - rect.height() / 2f - watchBackMargin,
            x + rect.width() / 2f + watchBackMargin,
            y + rect.height() / 2f + watchBackMargin
        )
        canvas.drawRoundRect(backRect, 10f, 10f, watchBackgroundPaint)
        canvas.drawText(text, x, y + rect.height() / 2f, watchPaint)
    }

    private val bitmapPaint = Paint()
    private fun drawFace(canvas: Canvas, x: Float, y: Float) {
        faceBitmap?.let {
            canvas.drawBitmap(
                it,
                x - faceImageSize / 2f,
                y - faceImageSize * 1.2f,
                bitmapPaint
            )
        }
    }

    private val eventYOffsetFactor = 0.15f
    private fun drawEvents(canvas: Canvas) {
        events.forEach { event ->
            rows.filter { it.start <= event.time && it.end > event.time }.forEach { row ->
                val currentX =
                    row.rectF.width() * (event.time - row.start).toFloat() / (row.end - row.start)
                canvas.drawBitmap(
                    event.bitmap,
                    currentX + textOffsetX,
                    row.rectF.top + (row.rectF.height() * eventYOffsetFactor),
                    bitmapPaint
                )
            }
        }
    }

    private val handlerCalendar = Calendar.getInstance()
    private fun getHandlerMsgDelay(): Long {
        handlerCalendar.timeInMillis = System.currentTimeMillis()
        return UPDATE_INTERVAL - handlerCalendar.get(Calendar.MILLISECOND)
    }

    fun startWatches() {
        watchHandler?.sendEmptyMessageDelayed(MSG_UPDATE_WATCHES, getHandlerMsgDelay())
    }

    fun stopWatches() {
        watchHandler?.removeMessages(MSG_UPDATE_WATCHES)
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_UPDATE_WATCHES -> {
                invalidate()
                watchHandler?.sendEmptyMessageDelayed(MSG_UPDATE_WATCHES, getHandlerMsgDelay())
            }
        }
        return true
    }

    class Row(
        val start: Long,
        val end: Long,
        val rectF: RectF,
        val color: Int
    ) {

        class RowInitializer(
            val start: Long,
            val end: Long,
            val color: Int
        )

    }

    class Event(
        val time: Long,
        val bitmap: Bitmap
    )

    init {
        setWillNotDraw(false)
    }

    companion object {
        const val MSG_UPDATE_WATCHES = 123
        const val UPDATE_INTERVAL = 1000L
    }

}