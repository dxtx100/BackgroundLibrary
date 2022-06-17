package com.noober.background.drawable

import android.content.res.TypedArray
import android.content.res.ColorStateList
import com.noober.background.R
import androidx.annotation.AttrRes

class ColorStateCreator internal constructor(
    private val textTa: TypedArray,
    private val color: Int
) {

    val colorList = mutableListOf<StateColor>()
    fun create(): ColorStateList {
        add(color, 0)
        for (i in 0 until textTa.indexCount) {
            val attr = textTa.getIndex(i)
            val c = textTa.getColor(attr, 0)
            if (attr == R.styleable.text_selector_bl_textColor_checked) {
                add(c, android.R.attr.state_checked)
            } else if (attr == R.styleable.text_selector_bl_textColor_unEnabled) {
                add(c, -android.R.attr.state_enabled)
            } else if (attr == R.styleable.text_selector_bl_textColor_selected) {
                add(c, android.R.attr.state_selected)
            } else if (attr == R.styleable.text_selector_bl_textColor_pressed) {
                add(c, android.R.attr.state_pressed)
            } else if (attr == R.styleable.text_selector_bl_textColor_focused) {
                add(c, android.R.attr.state_focused)
            }
        }
        return colorList.toColorStateList()
    }

    private fun add(color: Int, @AttrRes functionId: Int) {
        val state = if (color == 0) intArrayOf() else intArrayOf(functionId)
        colorList.add(StateColor(state, color))
    }
}