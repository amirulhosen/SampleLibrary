package com.example.mylibrary

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import androidx.annotation.DrawableRes
import android.util.AttributeSet
import android.util.StateSet

class CustomAnimatedButton : androidx.appcompat.widget.AppCompatButton {
    private var mPadding: Padding? = null
    private var mHeight: Int = 0
    private var mWidth: Int = 0
    private var mColor: Int = 0
    private var mCornerRadius: Int = 0
    private var mStrokeWidth: Int = 0
    private var mStrokeColor: Int = 0
    protected var mAnimationInProgress: Boolean = false
    var drawableNormal: StrokeGradientDrawable? = null
        private set
    private var mDrawablePressed: StrokeGradientDrawable? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mHeight == 0 && mWidth == 0 && w != 0 && h != 0) {
            mHeight = height
            mWidth = width
        }
    }

    fun animation(params: Params) {
        if (!mAnimationInProgress) {
            mDrawablePressed!!.color = params.colorPressed
            mDrawablePressed!!.setCornerRadius(params.cornerRadius.toFloat())
            mDrawablePressed!!.strokeColor = params.strokeColor
            mDrawablePressed!!.strokeWidth = params.strokeWidth
            if (params.duration == 0) {
                aniBtWithoutAnimation(params)
            } else {
                aniBtWithAnimation(params)
            }
            mColor = params.color
            mCornerRadius = params.cornerRadius
            mStrokeWidth = params.strokeWidth
            mStrokeColor = params.strokeColor
        }
    }

    private fun aniBtWithAnimation(params: Params) {
        mAnimationInProgress = true
        text = null
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        setPadding(mPadding!!.left, mPadding!!.top, mPadding!!.right, mPadding!!.bottom)
        val animationParams = CustomButtonAnimation.Params.create(this)
                .color(mColor, params.color)
                .cornerRadius(mCornerRadius, params.cornerRadius)
                .strokeWidth(mStrokeWidth, params.strokeWidth)
                .strokeColor(mStrokeColor, params.strokeColor)
                .height(height, params.height)
                .width(width, params.width)
                .duration(params.duration)
                .listener { finalizeAnimation(params) }
        val animation = CustomButtonAnimation(animationParams)
        animation.start()
    }

    private fun aniBtWithoutAnimation(params: Params) {
        drawableNormal!!.color = params.color
        drawableNormal!!.setCornerRadius(params.cornerRadius.toFloat())
        drawableNormal!!.strokeColor = params.strokeColor
        drawableNormal!!.strokeWidth = params.strokeWidth
        if (params.width != 0 && params.height != 0) {
            val layoutParams = layoutParams
            layoutParams.width = params.width
            layoutParams.height = params.height
            setLayoutParams(layoutParams)
        }
        finalizeAnimation(params)
    }

    private fun finalizeAnimation(params: Params) {
        mAnimationInProgress = false
        if (params.icon != 0 && params.text != null) {
            setIconLeft(params.icon)
            text = params.text
        } else if (params.icon != 0) {
            setIcon(params.icon)
        } else if (params.text != null) {
            text = params.text
        }
        if (params.animationListener != null) {
            params.animationListener!!.onAnimationEnd()
        }
    }

    fun blockTouch() {
        setOnTouchListener { v, event -> true }
    }

    fun unblockTouch() {
        setOnTouchListener { v, event -> false }
    }

    @SuppressLint("ResourceType")
    private fun initView() {
        mPadding = Padding()
        mPadding!!.left = paddingLeft
        mPadding!!.right = paddingRight
        mPadding!!.top = paddingTop
        mPadding!!.bottom = paddingBottom
        val resources = resources
        val cornerRadius = resources.getDimension(R.dimen.abc_action_bar_content_inset_material).toInt()
        val blue =Color.BLUE
        val blueDark = Color.BLUE
        val background = StateListDrawable()
        drawableNormal = createDrawable(blue, cornerRadius, 0)
        mDrawablePressed = createDrawable(blueDark, cornerRadius, 0)
        mColor = blue
        mStrokeColor = blue
        mCornerRadius = cornerRadius
        background.addState(intArrayOf(android.R.attr.state_pressed), mDrawablePressed!!.gradientDrawable)
        background.addState(StateSet.WILD_CARD, drawableNormal!!.gradientDrawable)
        setBackgroundCompat(background)
    }

    private fun createDrawable(color: Int, cornerRadius: Int, strokeWidth: Int): StrokeGradientDrawable {
        val drawable = StrokeGradientDrawable(GradientDrawable())
        drawable.gradientDrawable.shape = GradientDrawable.RECTANGLE
        drawable.color = color
        drawable.setCornerRadius(cornerRadius.toFloat())
        drawable.strokeColor = color
        drawable.strokeWidth = strokeWidth
        return drawable
    }

    private fun setBackgroundCompat(drawable: Drawable?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable)
        } else {
            background = drawable
        }
    }

    fun setIcon(@DrawableRes icon: Int) {
        // post is necessary, to make sure getWidth() doesn't return 0
        post {
            val drawable = resources.getDrawable(icon)
            val padding = width / 2 - drawable.intrinsicWidth / 2
            setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
            setPadding(padding, 0, 0, 0)
        }
    }

    fun setIconLeft(@DrawableRes icon: Int) {
        setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
    }

    private inner class Padding {
        var left: Int = 0
        var right: Int = 0
        var top: Int = 0
        var bottom: Int = 0
    }

    class Params private constructor() {
        var cornerRadius: Int = 0
        var width: Int = 0
        var height: Int = 0
        var color: Int = 0
        var colorPressed: Int = 0
        var duration: Int = 0
        var icon: Int = 0
        var strokeWidth: Int = 0
        var strokeColor: Int = 0
        var text: String? = null
        var animationListener: CustomButtonAnimation.Listener? = null
        fun text(text: String): Params {
            this.text = text
            return this
        }

        fun icon(@DrawableRes icon: Int): Params {
            this.icon = icon
            return this
        }

        fun cornerRadius(cornerRadius: Int): Params {
            this.cornerRadius = cornerRadius
            return this
        }

        fun width(width: Int): Params {
            this.width = width
            return this
        }

        fun height(height: Int): Params {
            this.height = height
            return this
        }

        fun color(color: Int): Params {
            this.color = color
            return this
        }

        fun colorPressed(colorPressed: Int): Params {
            this.colorPressed = colorPressed
            return this
        }

        fun duration(duration: Int): Params {
            this.duration = duration
            return this
        }

        fun strokeWidth(strokeWidth: Int): Params {
            this.strokeWidth = strokeWidth
            return this
        }

        fun strokeColor(strokeColor: Int): Params {
            this.strokeColor = strokeColor
            return this
        }

        fun animationListener(animationListener: CustomButtonAnimation.Listener): Params {
            this.animationListener = animationListener
            return this
        }

        companion object {
            fun create(): Params {
                return Params()
            }
        }
    }
}
