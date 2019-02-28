package com.example.mylibrary

import android.graphics.drawable.GradientDrawable

class StrokeGradientDrawable(val gradientDrawable: GradientDrawable) {
    var strokeWidth: Int = 0
        set(strokeWidth) {
            field = strokeWidth
            gradientDrawable.setStroke(strokeWidth, strokeColor)
        }
    var strokeColor: Int = 0
        set(strokeColor) {
            field = strokeColor
            gradientDrawable.setStroke(strokeWidth, strokeColor)
        }
    var radius: Float = 0.toFloat()
        private set
    var color: Int = 0
        set(color) {
            field = color
            gradientDrawable.setColor(color)
        }

    fun setCornerRadius(radius: Float) {
        this.radius = radius
        gradientDrawable.cornerRadius = radius
    }
}
