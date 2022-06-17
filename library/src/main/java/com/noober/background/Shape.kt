package com.noober.background

import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewOutlineProvider
import com.noober.background.drawable.StateColor
import com.noober.background.drawable.toColorStateList

/**
 * 描述：
 * <p>
 * author: Tiger
 * date: 2022/6/14
 * version 1.0
 */
class Shape(val typedArray: TypedArray) {

    var shape = 0

    var cornerRadius = FloatArray(4)

    var sizeWidth = 0
    var sizeHeight = 0
    var strokeWidth = -1f
    var strokeDashWidth = 0f
    var strokeColor = 0
    var solidColor = 0
    var strokeGap = 0f

    //渐变属性
    var centerX = 0f
    var centerY = 0f
    var centerColor = 0
    var startColor = 0
    var endColor = 0
    var gradientType = GradientDrawable.LINEAR_GRADIENT
    var gradientAngle = 0

    var padding = Rect()

    var gradientRadius = 0f
    var useLevel = false

    var solidColorState = mutableListOf<StateColor>()
    var strokeColorState = mutableListOf<StateColor>()

    init {
        for (i in 0 until typedArray.indexCount) {
            val attr = typedArray.getIndex(i)
            when (attr) {
                R.styleable.background_bl_shape ->
                    shape = typedArray.getInt(attr, 0)
                R.styleable.background_bl_corners_radius -> {
                    //与四个角共存,但设置四个角中某一个,取代对应的radius
                    val rr = typedArray.getDimension(attr, 0f)
                    for (j in 0 until 4) {
                        if (cornerRadius[j] == 0f) cornerRadius[j] = rr
                    }
                }
                R.styleable.background_bl_corners_bottomLeftRadius -> {
                    cornerRadius[3] = typedArray.getDimension(attr, 0f)
                    needCustomClip = true
                }
                R.styleable.background_bl_corners_bottomRightRadius -> {
                    cornerRadius[2] = typedArray.getDimension(attr, 0f)
                    needCustomClip = true
                }
                R.styleable.background_bl_corners_topLeftRadius -> {
                    cornerRadius[0] = typedArray.getDimension(attr, 0f)
                    needCustomClip = true
                }
                R.styleable.background_bl_corners_topRightRadius -> {
                    cornerRadius[1] = typedArray.getDimension(attr, 0f)
                    needCustomClip = true
                }
                R.styleable.background_bl_gradient_angle ->
                    gradientAngle = typedArray.getInteger(attr, 0)
                R.styleable.background_bl_gradient_centerX ->
                    centerX = typedArray.getFloat(attr, -1f)
                R.styleable.background_bl_gradient_centerY ->
                    centerY = typedArray.getFloat(attr, -1f)
                R.styleable.background_bl_gradient_centerColor ->
                    centerColor = typedArray.getColor(attr, 0)
                R.styleable.background_bl_gradient_endColor ->
                    endColor = typedArray.getColor(attr, 0)
                R.styleable.background_bl_gradient_startColor ->
                    startColor = typedArray.getColor(attr, 0)
                R.styleable.background_bl_gradient_gradientRadius ->
                    gradientRadius = typedArray.getDimension(attr, 0f)
                R.styleable.background_bl_gradient_type ->
                    gradientType = typedArray.getInt(attr, 0)
                R.styleable.background_bl_gradient_useLevel ->
                    useLevel = typedArray.getBoolean(attr, false)
                R.styleable.background_bl_padding_left ->
                    padding.left = typedArray.getDimension(attr, 0f).toInt()
                R.styleable.background_bl_padding_top ->
                    padding.top = typedArray.getDimension(attr, 0f).toInt()
                R.styleable.background_bl_padding_right ->
                    padding.right = typedArray.getDimension(attr, 0f).toInt()
                R.styleable.background_bl_padding_bottom ->
                    padding.bottom = typedArray.getDimension(attr, 0f).toInt()
                R.styleable.background_bl_size_width ->
                    sizeWidth = typedArray.getDimension(attr, 0f).toInt()
                R.styleable.background_bl_size_height ->
                    sizeHeight = typedArray.getDimension(attr, 0f).toInt()
                R.styleable.background_bl_stroke_width ->
                    strokeWidth = typedArray.getDimension(attr, 0f)

                R.styleable.background_bl_stroke_dashWidth ->
                    strokeDashWidth = typedArray.getDimension(attr, 0f)
                R.styleable.background_bl_stroke_dashGap ->
                    strokeGap = typedArray.getDimension(attr, 0f)


                R.styleable.background_bl_solid_color -> {
                    solidColor = typedArray.getColor(attr, 0)
                    addState(0, attr)
                }
                R.styleable.background_bl_stroke_color -> {
                    strokeColor = typedArray.getColor(attr, 0)
                    addState(0, attr, isStroke = true)
                }

                //状态颜色
                R.styleable.background_bl_solid_color_pressed -> {
                    addState(android.R.attr.state_pressed, attr)
                }
                R.styleable.background_bl_solid_color_checked -> {
                    addState(android.R.attr.state_checked, attr)
                }
                R.styleable.background_bl_solid_color_focused -> {
                    addState(android.R.attr.state_focused, attr)
                }
                R.styleable.background_bl_solid_color_selected -> {
                    addState(android.R.attr.state_selected, attr)
                }
                R.styleable.background_bl_solid_color_unEnabled -> {
                    addState(-android.R.attr.state_enabled, attr)
                }

                R.styleable.background_bl_stroke_color_pressed -> {
                    addState(android.R.attr.state_pressed, attr, true)
                }
                R.styleable.background_bl_stroke_color_checked -> {
                    addState(android.R.attr.state_checked, attr, true)
                }
                R.styleable.background_bl_stroke_color_focused -> {
                    addState(android.R.attr.state_focused, attr, true)
                }
                R.styleable.background_bl_stroke_color_selected -> {
                    addState(android.R.attr.state_selected, attr, true)
                }
                R.styleable.background_bl_stroke_color_unEnabled -> {
                    addState(-android.R.attr.state_enabled, attr, true)
                }
            }
        }
//        typedArray.recycle()
    }

    private fun addState(state: Int, attr: Int, isStroke: Boolean = false) {
        val color = typedArray.getColor(attr, 0)
        val stateColor = StateColor(if (state == 0) intArrayOf() else intArrayOf(state), color)
        if (isStroke) {
            strokeColorState.add(stateColor)
        } else {
            solidColorState.add(stateColor)
        }
    }

    fun hasColor(): Boolean {
        return solidColor != 0 || strokeColor != 0 || startColor != 0 || endColor != 0 || centerColor != 0
    }

    fun hasCorner(): Boolean {
        for (f in cornerRadius) if (f != 0f) return true
        return false
    }

    var needCustomClip = false

    fun hasGradient(): Boolean {
        return startColor != 0 && endColor != 0
    }

    fun getSolidColor() = solidColorState.toColorStateList()

    fun getStrokeColor() = strokeColorState.toColorStateList()

    private fun getCorner(): Float {
        var r = 0f
        for (radius in cornerRadius) {
            if (r == 0f) r = radius
            //四个角不统一,无法裁剪,clipPath目前还有很多问题
            else if (r != radius) return 0f
        }
        return r
    }

    val outlineProvider: ViewOutlineProvider by lazy {
        object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                if (shape == 2) {
                    outline.setOval(0, 0, view.width, view.height)
                } else {
                    outline.setRoundRect(0, 0, view.width, view.height, getCorner())
                }

                if (view.elevation == 0f && view.translationZ <= 0) {
                    //没有海拔,则不需要显示阴影,不需要透明度
                    outline.alpha = 0f
                } else {
                    //需要显示轮廓阴影
                    outline.alpha = 1f
                }
            }
        }
    }
}