package com.aitsuki.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import com.aitsuki.view.R
import kotlin.math.roundToInt

class CodeEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : EditText(context, attrs) {

    companion object {
        private const val DEFAULT_BOX_COUNT = 6
    }

    private val dp = context.resources.displayMetrics.density
    private var spacing = 8 * dp
    private var boxPadding = 8 * dp
    private var boxRatio = 1f
    private var stroke = 1 * dp
    private var radius = 5 * dp
    private var boxColor = Color.TRANSPARENT
    private var strokeColor = Color.GRAY
    private var focusColor = Color.CYAN

    private var boxWidth = 0f
    private var boxHeight = 0f
    private var boxCount = 0
    private val textWidths: FloatArray
    private val boxPaint: Paint
    private val strokePaint: Paint
    private val focusPaint: Paint

    init {
        background = null
        setPadding(0, 0, 0, 0)
        movementMethod = null
        isCursorVisible = false
        maxLines = 1
        isSingleLine = false

        var maxLength = getMaxLength()
        if (maxLength == -1) {
            maxLength = DEFAULT_BOX_COUNT
            setMaxLength(maxLength)
        }
        boxCount = maxLength
        textWidths = FloatArray(maxLength)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CodeEditText)
            spacing = a.getDimension(R.styleable.CodeEditText_boxSpacing, spacing)
            boxPadding = a.getDimension(R.styleable.CodeEditText_boxPadding, boxPadding)
            radius = a.getDimension(R.styleable.CodeEditText_boxRadius, radius)
            boxRatio = a.getFloat(R.styleable.CodeEditText_boxRatio, boxRatio)
            stroke = a.getDimension(R.styleable.CodeEditText_boxStroke, stroke)
            boxColor = a.getColor(R.styleable.CodeEditText_boxColor, boxColor)
            strokeColor = a.getColor(R.styleable.CodeEditText_boxStrokeColor, strokeColor)
            focusColor = a.getColor(R.styleable.CodeEditText_boxStrokeFocusedColor, focusColor)
            a.recycle()
        }

        boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = boxColor
            it.style = Paint.Style.FILL
        }

        strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = strokeColor
            it.style = Paint.Style.STROKE
            it.strokeWidth = stroke
        }

        focusPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = focusColor
            it.style = Paint.Style.STROKE
            it.strokeWidth = stroke
        }
    }

    private fun getMaxLength(): Int {
        for (filter in filters) {
            if (filter is LengthFilter) {
                return filter.max
            }
        }
        return -1
    }

    private fun setMaxLength(max: Int) {
        filters = Array<InputFilter>(filters.size + 1) { i ->
            if (i == 0) LengthFilter(max) else filters[i - 1]
        }
        if (length() > max) {
            text = text.delete(max, length())
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        if (text != null) {
            if (selStart != text.length || selEnd != text.length) {
                setSelection(text.length, text.length)
                return
            }
        }
        super.onSelectionChanged(selStart, selEnd)
    }

    /**
     * 测量规则
     * - at most:
     *      - 宽度：字体大小 + boxPadding + boxSpacing
     *      - 高度：宽度 / boxRatio
     * - exactly:
     *      - 宽度：控件宽度平分，忽略 boxPadding
     *      - 高度：控件高度
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (boxCount == 0) {
            setMeasuredDimension(0, 0)
            return
        }

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val width: Float
        val totalSpacing = spacing * (boxCount - 1)
        if (widthMode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
            boxWidth = (width - totalSpacing) / boxCount - stroke
        } else {
            boxWidth = textSize + boxPadding * 2
            width = (boxWidth + stroke) * boxCount + stroke * 0.5f + totalSpacing
        }

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val height: Float
        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
            boxHeight = height - stroke
        } else {
            boxHeight = boxWidth / boxRatio
            height = boxHeight + stroke * 1.5f
        }
        setMeasuredDimension((width + 0.5f).roundToInt(), (height + 0.5f).roundToInt())
    }

    override fun onDraw(canvas: Canvas) {
        val t = stroke
        val b = t + boxHeight
        var l = stroke
        for (i in 0 until boxCount) {
            val r = l + boxWidth
            canvas.drawRoundRect(l, t, r, b, radius, radius, boxPaint)
            if (hasFocus() && selectionStart == i) {
                canvas.drawRoundRect(l, t, r, b, radius, radius, focusPaint)
            } else {
                canvas.drawRoundRect(l, t, r, b, radius, radius, strokePaint)
            }
            paint.getTextWidths(text, 0, text.length, textWidths)
            paint.color = currentTextColor
            if (text.length > i) {
                val charX = l + boxWidth * 0.5f - textWidths[i] * 0.5f
                canvas.drawText(text, i, i + 1, charX, baseline.toFloat(), paint)
            }
            l = r + spacing + stroke
        }
    }
}
