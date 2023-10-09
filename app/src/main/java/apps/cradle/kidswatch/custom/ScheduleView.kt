package apps.cradle.kidswatch.custom

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
import androidx.preference.PreferenceManager
import apps.cradle.kidswatch.MainViewModel
import apps.cradle.kidswatch.R
import apps.cradle.kidswatch.fragments.MainFragment.Companion.DEFAULT_INTERVALS
import apps.cradle.kidswatch.fragments.MainFragment.Companion.PREF_INTERVALS
import apps.cradle.kidswatch.fragments.SettingsFragment
import apps.cradle.kidswatch.fragments.SettingsFragment.Companion.PREF_TIME_FORMAT
import apps.cradle.kidswatch.fragments.SettingsFragment.Companion.TIME_FORMAT_12
import apps.cradle.kidswatch.fragments.SettingsFragment.Companion.TIME_FORMAT_24
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ScheduleView(context: Context, attrs: AttributeSet) : View(context, attrs), Handler.Callback {

    private var watchHandler: Handler? = null
    private val scheduleTimeFormat: SimpleDateFormat
    private val watchesTimeFormat: SimpleDateFormat
    private val rows = mutableListOf<Row>()
    private var faceBitmap: Bitmap? = null
    private val events = mutableListOf<Event>()
    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    private val faceImageSize: Int
    private val eventImageSize: Int
    private val watchTextSize: Float
    private val hoursTextSize: Float
    private val textOffsetX: Float
    private val textOffsetY: Float
    private val safeZonePx: Int

    init {
        scheduleTimeFormat = when (prefs.getInt(PREF_TIME_FORMAT, TIME_FORMAT_24)) {
            TIME_FORMAT_12 -> SimpleDateFormat("h:mm", Locale.getDefault())
            else -> SimpleDateFormat("H:mm", Locale.getDefault())
        }
        watchesTimeFormat = when (prefs.getInt(PREF_TIME_FORMAT, TIME_FORMAT_24)) {
            TIME_FORMAT_12 -> SimpleDateFormat("h:mm:ss", Locale.getDefault())
            else -> SimpleDateFormat("H:mm:ss", Locale.getDefault())
        }
        val intervals = prefs.getString(PREF_INTERVALS, null) ?: DEFAULT_INTERVALS
        val intervalsCount = MainViewModel.getPointFromIntervalsString(intervals).size + 1
        val screenHeightPx = context.resources.displayMetrics.heightPixels
        faceImageSize = (0.6 * (screenHeightPx / (intervalsCount))).toInt()
        eventImageSize = (0.5 * (screenHeightPx / (intervalsCount))).toInt()
        watchTextSize = (0.15f * (screenHeightPx / (intervalsCount)))
        hoursTextSize = (0.15f * (screenHeightPx / (intervalsCount)))
        textOffsetX = (0.05f * (screenHeightPx / (intervalsCount)))
        textOffsetY = (0.2f * (screenHeightPx / (intervalsCount)))
        safeZonePx = (0.4 * (screenHeightPx / (intervalsCount))).toInt()
        watchHandler = Handler(Looper.getMainLooper(), this)
    }

    fun initialize(rows: List<Row.RowInitializer>) {
        initFaceImage()
        doOnLayout {
            createRectFs(rows)
            invalidate()
        }
    }

    fun addEvent(time: Long, fileName: String) {
        var bitmap = BitmapFactory.decodeStream(context.assets.open(fileName))
        bitmap = Bitmap.createScaledBitmap(bitmap, eventImageSize, eventImageSize, true)
        events.add(Event(time, bitmap))
    }

    private fun initFaceImage() {
        faceBitmap = BitmapFactory.decodeResource(
            context.resources,
            when (prefs.getInt(SettingsFragment.PREF_SEX, SettingsFragment.SEX_MALE)) {
                SettingsFragment.SEX_MALE -> R.drawable.image_face_boy
                else -> R.drawable.image_face_girl
            }
        )
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

    override fun onDraw(canvas: Canvas) {
        canvas.let {
            drawRows(canvas)
            drawHours(canvas)
            drawEvents(canvas)
            drawOverlay(canvas)
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
        textSize = hoursTextSize
        typeface = Typeface.DEFAULT_BOLD
        style = Paint.Style.FILL
        isAntiAlias = true
    }

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
                        scheduleTimeFormat.format(Date(i)),
                        x + textOffsetX,
                        row.rectF.top + textOffsetY,
                        textPaint
                    )
                }
            }
        }
    }

    private val verticalOffsetFactor = 0.35f
    private fun drawOverlay(canvas: Canvas) {
        rows.forEach { row ->
            val current = Calendar.getInstance()
            val currentY = row.rectF.centerY() + row.rectF.height() * verticalOffsetFactor
            when {
                current.timeInMillis >= row.start && current.timeInMillis < row.end -> {
                    var currentX =
                        row.rectF.width() * (current.timeInMillis - row.start).toFloat() / (row.end - row.start)
                    when {
                        currentX + safeZonePx > row.rectF.right ->
                            currentX = row.rectF.right - safeZonePx

                        currentX - safeZonePx < row.rectF.left ->
                            currentX = row.rectF.left + safeZonePx
                    }
                    drawFace(canvas, currentX, currentY)
                    drawWatches(canvas, currentX, currentY)
                }
            }
        }
    }

    private val watchPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.watchTextColor)
        textAlign = Paint.Align.CENTER
        textSize = watchTextSize
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }
    private val watchBackgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val watchBackMargin = 15f
    private fun drawWatches(canvas: Canvas, x: Float, y: Float) {
        val text = watchesTimeFormat.format(Date())
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

    private val eventYOffsetFactor = 0.25f
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