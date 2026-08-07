package com.dinusha.mindmate_sl.ui.journey

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class WeeklyStressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val stressValues = MutableList<Float?>(7) { null }

    private val days = listOf(
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat",
        "Sun"
    )

    private val gridPaint = Paint().apply {
        color = Color.parseColor("#D9E7E4")
        strokeWidth = 1f
    }

    private val linePaint = Paint().apply {
        color = Color.parseColor("#00796B")
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private val pointPaint = Paint().apply {
        color = Color.parseColor("#004D40")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.parseColor("#607D7B")
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val labelPaint = Paint().apply {
        color = Color.parseColor("#607D7B")
        textSize = 23f
        isAntiAlias = true
    }

    fun setStressData(values: List<Float?>) {

        for (i in 0 until 7) {
            stressValues[i] =
                if (i < values.size) values[i]
                else null
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val leftPadding = 65f
        val rightPadding = 25f
        val topPadding = 25f
        val bottomPadding = 50f

        val chartWidth =
            width - leftPadding - rightPadding

        val chartHeight =
            height - topPadding - bottomPadding

        // Horizontal grid
        for (i in 0..4) {

            val y =
                topPadding + chartHeight * i / 4

            canvas.drawLine(
                leftPadding,
                y,
                width - rightPadding,
                y,
                gridPaint
            )

            val value =
                100 - (i * 25)

            canvas.drawText(
                value.toString(),
                5f,
                y + 8,
                labelPaint
            )
        }

        val stepX =
            chartWidth / 6f

        // Day labels
        for (i in days.indices) {

            val x =
                leftPadding + stepX * i

            canvas.drawText(
                days[i],
                x,
                height - 10f,
                textPaint
            )
        }

        val path = Path()

        var hasStarted = false

        for (i in stressValues.indices) {

            val value =
                stressValues[i] ?: continue

            val x =
                leftPadding + stepX * i

            val y =
                topPadding +
                        chartHeight *
                        (1f - value / 100f)

            if (!hasStarted) {
                path.moveTo(x, y)
                hasStarted = true
            } else {
                path.lineTo(x, y)
            }

            canvas.drawCircle(
                x,
                y,
                8f,
                pointPaint
            )
        }

        if (hasStarted) {
            canvas.drawPath(
                path,
                linePaint
            )
        }
    }
}