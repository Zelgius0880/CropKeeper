package com.zelgius.cropkeeper.ui.generic

import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RangeSlider(
    valueFrom: Float,
    valueTo: Float,
    modifier: Modifier = Modifier,
    stepSize: Float = 0f,
    values: List<Float> = listOf(valueFrom, valueTo),
    isTickVisible: Boolean = true,
    formatter: ((Float) -> String)? = null,
    trackColor: Color = MaterialTheme.colorScheme.primary,
    inactiveTrackColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
    thumbColor: Color = MaterialTheme.colorScheme.primary,
    onValueChange: ((ClosedFloatingPointRange<Float>) -> Unit)? = null
) {
    AndroidView(modifier = modifier, factory = {
        val parent = FrameLayout(it)
        parent.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        com.google.android.material.slider.RangeSlider(it).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            this.valueFrom = valueFrom
            this.valueTo = valueTo
            this.values = values

            this.stepSize = stepSize

            this.isTickVisible = isTickVisible

            this.setLabelFormatter(formatter)
            this.addOnChangeListener { slider, _, fromUser ->
                if(fromUser) onValueChange?.invoke((slider.values.first() .. slider.values.last()))
            }
        }
        //parent
    }, update = {
        it.trackTintList = ColorStateList.valueOf(trackColor.toArgb())
        it.trackInactiveTintList = ColorStateList.valueOf(inactiveTrackColor.toArgb())
        it.thumbTintList = ColorStateList.valueOf(thumbColor.toArgb())
    })
}