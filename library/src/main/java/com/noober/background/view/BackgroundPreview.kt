package com.noober.background.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import com.noober.background.R
import com.noober.background.Shape
import com.noober.background.drawable.DrawableFactory

internal object BackgroundPreview {

    @JvmStatic
    fun setViewBackground(context: Context, attrs: AttributeSet?, view: View): View? {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.background)
        val selectorTa = context.obtainStyledAttributes(attrs, R.styleable.background_selector)
        val textTa = context.obtainStyledAttributes(attrs, R.styleable.text_selector)
        val buttonTa = context.obtainStyledAttributes(attrs, R.styleable.background_button_drawable)
        val otherTa = context.obtainStyledAttributes(attrs, R.styleable.bl_other)

        val shape = Shape(typedArray)
        return try {
            if (!shape.hasColor() && !shape.hasCorner() && !shape.hasGradient() && textTa.indexCount == 0 && buttonTa.indexCount == 0) {
                return view
            }
            var drawable: GradientDrawable? = null
            var stateListDrawable: StateListDrawable? = null
            if (buttonTa.indexCount > 0 && view is CompoundButton) {
                view.setClickable(true)
                view.buttonDrawable = DrawableFactory.getButtonDrawable(typedArray, buttonTa)
            } else if (selectorTa.indexCount > 0) {
                stateListDrawable = DrawableFactory.getSelectorDrawable(typedArray, selectorTa)
                view.isClickable = true
                setDrawable(stateListDrawable, view, otherTa)
            } else if (shape.hasColor()) {
                drawable = DrawableFactory.getDrawable(shape)
                setDrawable(drawable, view, otherTa)
            }
            if (view is TextView && textTa.indexCount > 0) {
                //取默认颜色混合state
                val ta = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.textColor))
                val color = ta.getColor(0, 0)
                ta.recycle()
                view.setTextColor(DrawableFactory.getTextSelectorColor(textTa, color))
            }
            //去除水波纹的预览

            //是否裁剪
            if (otherTa.hasValue(R.styleable.bl_other_bl_isClip)) {
                val isClip = otherTa.getBoolean(R.styleable.bl_other_bl_isClip, false)
                if (isClip) {
                    view.outlineProvider = shape.outlineProvider
                    view.clipToOutline = true
                }
            }
            view
        } catch (e: Exception) {
            e.printStackTrace()
            view
        } finally {
            typedArray.recycle()
            selectorTa.recycle()
            textTa.recycle()
            buttonTa.recycle()
            otherTa.recycle()
        }
    }

    private fun setDrawable(drawable: Drawable?, view: View, otherTa: TypedArray) {
        if (view is TextView) {
            if (otherTa.hasValue(R.styleable.bl_other_bl_position)) {
                if (otherTa.getInt(R.styleable.bl_other_bl_position, 0) == 1) {
                    drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    view.setCompoundDrawables(drawable, null, null, null)
                } else if (otherTa.getInt(R.styleable.bl_other_bl_position, 0) == 2) {
                    drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    view.setCompoundDrawables(null, drawable, null, null)
                } else if (otherTa.getInt(R.styleable.bl_other_bl_position, 0) == 4) {
                    drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    view.setCompoundDrawables(null, null, drawable, null)
                } else if (otherTa.getInt(R.styleable.bl_other_bl_position, 0) == 8) {
                    drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    view.setCompoundDrawables(null, null, null, drawable)
                }
            } else {
                view.setBackground(drawable)
            }
        } else {
            view.background = drawable
        }
    }
}