package com.dinusha.mindmate_sl.ui.journey

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt


class WeeklyStressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {


    // ============================================================
    // DATA
    // ============================================================

    private val stressValues =
        MutableList<Float?>(7) {
            null
        }


    private val days = listOf(
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat",
        "Sun"
    )


    // ============================================================
    // COLORS
    // ============================================================

    private val teal =
        Color.parseColor("#00897B")

    private val darkTeal =
        Color.parseColor("#00695C")

    private val gridColor =
        Color.parseColor("#E3ECEA")

    private val axisTextColor =
        Color.parseColor("#78909C")

    private val emptyTextColor =
        Color.parseColor("#607D7B")

    private val white =
        Color.WHITE


    // ============================================================
    // PAINTS
    // ============================================================

    private val gridPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = gridColor

            strokeWidth =
                dp(1f)

            style =
                Paint.Style.STROKE
        }


    private val linePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = teal

            strokeWidth =
                dp(3.5f)

            style =
                Paint.Style.STROKE

            strokeCap =
                Paint.Cap.ROUND

            strokeJoin =
                Paint.Join.ROUND
        }


    private val pointPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = darkTeal

            style =
                Paint.Style.FILL
        }


    private val pointOuterPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = white

            style =
                Paint.Style.FILL
        }


    private val latestPointPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = teal

            style =
                Paint.Style.FILL
        }


    private val dayTextPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = axisTextColor

            textSize =
                sp(11f)

            textAlign =
                Paint.Align.CENTER
        }


    private val yAxisTextPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = axisTextColor

            textSize =
                sp(10f)

            textAlign =
                Paint.Align.RIGHT
        }


    private val valueTextPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = darkTeal

            textSize =
                sp(10f)

            textAlign =
                Paint.Align.CENTER

            isFakeBoldText =
                true
        }


    private val emptyTitlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = emptyTextColor

            textSize =
                sp(14f)

            textAlign =
                Paint.Align.CENTER

            isFakeBoldText =
                true
        }


    private val emptySubtitlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = axisTextColor

            textSize =
                sp(11f)

            textAlign =
                Paint.Align.CENTER
        }


    // ============================================================
    // PUBLIC DATA FUNCTION
    // KEEP THIS NAME - JourneyFragment can continue using it
    // ============================================================

    fun setStressData(
        values: List<Float?>
    ) {

        for (
        i in 0 until 7
        ) {

            stressValues[i] =
                if (
                    i < values.size
                ) {

                    values[i]
                        ?.coerceIn(
                            0f,
                            100f
                        )

                } else {

                    null
                }
        }


        invalidate()
    }


    // ============================================================
    // DRAW
    // ============================================================

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )


        if (
            width <= 0
            ||
            height <= 0
        ) {

            return
        }


        // --------------------------------------------------------
        // Chart padding
        // --------------------------------------------------------

        val leftPadding =
            dp(42f)

        val rightPadding =
            dp(14f)

        val topPadding =
            dp(24f)

        val bottomPadding =
            dp(40f)


        val chartWidth =
            width -
                    leftPadding -
                    rightPadding


        val chartHeight =
            height -
                    topPadding -
                    bottomPadding


        if (
            chartWidth <= 0f
            ||
            chartHeight <= 0f
        ) {

            return
        }


        // --------------------------------------------------------
        // Grid + Y-axis
        // 100 / 75 / 50 / 25 / 0
        // --------------------------------------------------------

        val yAxisValues =
            listOf(
                100,
                75,
                50,
                25,
                0
            )


        for (
        index in yAxisValues.indices
        ) {

            val y =
                topPadding +
                        (
                                chartHeight *
                                        index /
                                        4f
                                )


            // Grid line
            canvas.drawLine(

                leftPadding,

                y,

                width -
                        rightPadding,

                y,

                gridPaint
            )


            // Y-axis label
            canvas.drawText(

                yAxisValues[index]
                    .toString(),

                leftPadding -
                        dp(9f),

                y +
                        dp(3.5f),

                yAxisTextPaint
            )
        }


        // --------------------------------------------------------
        // X positions
        // --------------------------------------------------------

        val stepX =
            chartWidth /
                    6f


        // --------------------------------------------------------
        // Day labels
        // --------------------------------------------------------

        for (
        i in days.indices
        ) {

            val x =
                leftPadding +
                        stepX *
                        i


            canvas.drawText(

                days[i],

                x,

                height -
                        dp(9f),

                dayTextPaint
            )
        }


        // --------------------------------------------------------
        // EMPTY STATE
        // --------------------------------------------------------

        val hasAnyData =
            stressValues.any {
                it != null
            }


        if (
            !hasAnyData
        ) {

            drawEmptyState(
                canvas
            )

            return
        }


        // --------------------------------------------------------
        // DRAW LINE SEGMENTS
        //
        // IMPORTANT:
        // If a day has no data, we do NOT draw a fake line across
        // the missing day.
        // --------------------------------------------------------

        var segmentPath =
            Path()

        var segmentStarted =
            false


        for (
        i in stressValues.indices
        ) {

            val value =
                stressValues[i]


            if (
                value == null
            ) {

                if (
                    segmentStarted
                ) {

                    canvas.drawPath(
                        segmentPath,
                        linePaint
                    )
                }


                segmentPath =
                    Path()

                segmentStarted =
                    false

                continue
            }


            val x =
                calculateX(
                    index = i,
                    leftPadding = leftPadding,
                    stepX = stepX
                )


            val y =
                calculateY(
                    value = value,
                    topPadding = topPadding,
                    chartHeight = chartHeight
                )


            if (
                !segmentStarted
            ) {

                segmentPath.moveTo(
                    x,
                    y
                )

                segmentStarted =
                    true

            } else {

                segmentPath.lineTo(
                    x,
                    y
                )
            }
        }


        if (
            segmentStarted
        ) {

            canvas.drawPath(
                segmentPath,
                linePaint
            )
        }


        // --------------------------------------------------------
        // DATA POINTS
        // --------------------------------------------------------

        val latestDataIndex =
            stressValues
                .indexOfLast {
                    it != null
                }


        for (
        i in stressValues.indices
        ) {

            val value =
                stressValues[i]
                    ?: continue


            val x =
                calculateX(
                    index = i,
                    leftPadding = leftPadding,
                    stepX = stepX
                )


            val y =
                calculateY(
                    value = value,
                    topPadding = topPadding,
                    chartHeight = chartHeight
                )


            // White outer circle
            canvas.drawCircle(

                x,

                y,

                dp(6.5f),

                pointOuterPaint
            )


            // Main point
            canvas.drawCircle(

                x,

                y,

                if (
                    i == latestDataIndex
                ) {
                    dp(5f)
                } else {
                    dp(4f)
                },

                if (
                    i == latestDataIndex
                ) {
                    latestPointPaint
                } else {
                    pointPaint
                }
            )


            // Latest value label
            if (
                i == latestDataIndex
            ) {

                drawLatestValue(
                    canvas = canvas,
                    value = value,
                    x = x,
                    y = y,
                    topPadding = topPadding
                )
            }
        }
    }


    // ============================================================
    // EMPTY STATE
    // ============================================================

    private fun drawEmptyState(
        canvas: Canvas
    ) {

        val centerX =
            width /
                    2f


        val centerY =
            height /
                    2f -
                    dp(5f)


        canvas.drawText(

            "No stress data yet",

            centerX,

            centerY,

            emptyTitlePaint
        )


        canvas.drawText(

            "Chat with MindMate to build your weekly trend",

            centerX,

            centerY +
                    dp(22f),

            emptySubtitlePaint
        )
    }


    // ============================================================
    // LATEST VALUE
    // ============================================================

    private fun drawLatestValue(
        canvas: Canvas,
        value: Float,
        x: Float,
        y: Float,
        topPadding: Float
    ) {

        val valueText =
            value
                .roundToInt()
                .toString()


        var textY =
            y -
                    dp(12f)


        // Avoid clipping at top
        if (
            textY <
            topPadding +
            dp(10f)
        ) {

            textY =
                y +
                        dp(18f)
        }


        canvas.drawText(

            valueText,

            x,

            textY,

            valueTextPaint
        )
    }


    // ============================================================
    // POSITION HELPERS
    // ============================================================

    private fun calculateX(
        index: Int,
        leftPadding: Float,
        stepX: Float
    ): Float {

        return leftPadding +
                stepX *
                index
    }


    private fun calculateY(
        value: Float,
        topPadding: Float,
        chartHeight: Float
    ): Float {

        val safeValue =
            value.coerceIn(
                0f,
                100f
            )


        return topPadding +
                chartHeight *
                (
                        1f -
                                safeValue /
                                100f
                        )
    }


    // ============================================================
    // UNIT HELPERS
    // ============================================================

    private fun dp(
        value: Float
    ): Float {

        return value *
                resources
                    .displayMetrics
                    .density
    }


    private fun sp(
        value: Float
    ): Float {

        return value *
                resources
                    .displayMetrics
                    .scaledDensity
    }
}