package com.noober.background.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.*
import com.noober.background.BackgroundFactory

/**
 * 预览方式
 */
private fun View.bl(a: AttributeSet?) {
    if (isInEditMode) {
        //工厂部分代码造成预览错误,这里关掉水波纹等预览
        BackgroundPreview.setViewBackground(context, a, this)
    } else {
        BackgroundFactory.setViewBackground(context, a, this)
    }
}

/**
 * 一系列可以预览的控件
 */

class BLTextView(c: Context, a: AttributeSet?) : AppCompatTextView(c, a) {
    init {
        bl(a)
    }
}

class BLButton(c: Context, a: AttributeSet?) : AppCompatButton(c, a) {
    init {
        bl(a)
    }
}

class BLCheckBox(c: Context, a: AttributeSet?) : AppCompatCheckBox(c, a) {
    init {
        bl(a)
    }
}

class BLEditText(c: Context, a: AttributeSet?) : AppCompatEditText(c, a) {
    init {
        bl(a)
    }
}

class BLFrameLayout(c: Context, a: AttributeSet?) : FrameLayout(c, a) {
    init {
        bl(a)
    }
}

class BLImageView(c: Context, a: AttributeSet?) : AppCompatImageView(c, a) {
    init {
        bl(a)
    }
}

class BLView(c: Context, a: AttributeSet?) : View(c, a) {
    init {
        bl(a)
    }
}

class BLLinearLayout(c: Context, a: AttributeSet) : LinearLayout(c, a) {
    init {
        bl(a)
    }
}

class BLRelativeLayout(c: Context, a: AttributeSet) : RelativeLayout(c, a) {
    init {
        bl(a)
    }
}

class BLRadioButton(c: Context, a: AttributeSet) : AppCompatRadioButton(c, a) {
    init {
        bl(a)
    }
}