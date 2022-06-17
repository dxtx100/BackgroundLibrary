package com.noober.background.drawable

import android.content.res.ColorStateList

/**
 * 描述：
 * <p>
 * author: Tiger
 * date: 2022/6/16
 * version 1.0
 */
class StateColor(val state: IntArray, val color: Int)


fun MutableList<StateColor>.toColorStateList(): ColorStateList {
    //排序
    this.sortBy {
        if (it.state.isEmpty() || it.state[0] == 0) return@sortBy 99
        return@sortBy when (it.state[0]) {
            -android.R.attr.state_enabled -> 1
            android.R.attr.state_pressed -> 2
            android.R.attr.state_checked -> 3
            android.R.attr.state_focused -> 4
            android.R.attr.state_selected -> 5
            else -> 98
        }
    }
    return ColorStateList(
        map { it.state }.toTypedArray(),
        map { it.color }.toIntArray()
    )
}