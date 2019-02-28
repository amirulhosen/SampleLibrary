package com.example.mylibrary

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator

class CustomButtonAnimation(private val mParams: Params) {
    interface Listener {
        fun onAnimationEnd()
    }

    class Params private constructor(val button: CustomAnimatedButton) {
        var fromCornerRadius: Float = 0.toFloat()
        var toCornerRadius: Float = 0.toFloat()
        var fromHeight: Int = 0
        var toHeight: Int = 0
        var fromWidth: Int = 0
        var toWidth: Int = 0
        var fromColor: Int = 0
        var toColor: Int = 0
        var duration: Int = 0
        var fromStrokeWidth: Int = 0
        var toStrokeWidth: Int = 0
        var fromStrokeColor: Int = 0
        var toStrokeColor: Int = 0
        var animationListener: CustomButtonAnimation.Listener? = null
        fun duration(duration: Int): Params {
            this.duration = duration
            return this
        }

        fun listener(animationListener: () -> Unit): Params {
//            this.animationListener = animationListener
            return this
        }

        fun color(fromColor: Int, toColor: Int): Params {
            this.fromColor = fromColor
            this.toColor = toColor
            return this
        }

        fun cornerRadius(fromCornerRadius: Int, toCornerRadius: Int): Params {
            this.fromCornerRadius = fromCornerRadius.toFloat()
            this.toCornerRadius = toCornerRadius.toFloat()
            return this
        }

        fun height(fromHeight: Int, toHeight: Int): Params {
            this.fromHeight = fromHeight
            this.toHeight = toHeight
            return this
        }

        fun width(fromWidth: Int, toWidth: Int): Params {
            this.fromWidth = fromWidth
            this.toWidth = toWidth
            return this
        }

        fun strokeWidth(fromStrokeWidth: Int, toStrokeWidth: Int): Params {
            this.fromStrokeWidth = fromStrokeWidth
            this.toStrokeWidth = toStrokeWidth
            return this
        }

        fun strokeColor(fromStrokeColor: Int, toStrokeColor: Int): Params {
            this.fromStrokeColor = fromStrokeColor
            this.toStrokeColor = toStrokeColor
            return this
        }

        companion object {
            fun create(button: CustomAnimatedButton): Params {
                return Params(button)
            }
        }
    }

    fun start() {
        val background = mParams.button.drawableNormal
        val cornerAnimation = ObjectAnimator.ofFloat(background, "cornerRadius", mParams.fromCornerRadius, mParams.toCornerRadius)
        val strokeWidthAnimation = ObjectAnimator.ofInt(background, "strokeWidth", mParams.fromStrokeWidth, mParams.toStrokeWidth)
        val strokeColorAnimation = ObjectAnimator.ofInt(background, "strokeColor", mParams.fromStrokeColor, mParams.toStrokeColor)
        strokeColorAnimation.setEvaluator(ArgbEvaluator())
        val bgColorAnimation = ObjectAnimator.ofInt(background, "color", mParams.fromColor, mParams.toColor)
        bgColorAnimation.setEvaluator(ArgbEvaluator())
        val heightAnimation = ValueAnimator.ofInt(mParams.fromHeight, mParams.toHeight)
        heightAnimation.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams = mParams.button.layoutParams
            layoutParams.height = `val`
            mParams.button.layoutParams = layoutParams
        }
        val widthAnimation = ValueAnimator.ofInt(mParams.fromWidth, mParams.toWidth)
        widthAnimation.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams = mParams.button.layoutParams
            layoutParams.width = `val`
            mParams.button.layoutParams = layoutParams
        }
        val animatorSet = AnimatorSet()
        animatorSet.duration = mParams.duration.toLong()
        animatorSet.playTogether(strokeWidthAnimation, strokeColorAnimation, cornerAnimation, bgColorAnimation,
                heightAnimation, widthAnimation)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (mParams.animationListener != null) {
                    mParams.animationListener!!.onAnimationEnd()
                }
            }
        })
        animatorSet.start()
    }
}