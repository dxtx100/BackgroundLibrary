package com.noober.background.drawable

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.RequiresPermission
import com.noober.background.Shape
import org.xmlpull.v1.XmlPullParserException

internal class GradientDrawableCreator(val shape: Shape) : ICreateDrawable {
//    private var typedArray: TypedArray? = null

    @Throws(XmlPullParserException::class)
    override fun create(): Drawable {
        val drawable = GradientDrawable()
        drawable.shape = shape.shape
        drawable.gradientRadius = shape.gradientRadius
        drawable.gradientType = shape.gradientType
        drawable.useLevel = shape.useLevel

        if (shape.hasCorner()) {
            val rs = mutableListOf<Float>()
            for (i in shape.cornerRadius) {
                rs.add(i)
                rs.add(i)
            }
            drawable.cornerRadii = rs.toFloatArray()
        }

        if (shape.sizeWidth > 0 && shape.sizeHeight > 0) {
            drawable.setSize(shape.sizeWidth, shape.sizeHeight)
        }


        //设置填充颜色
        drawable.color = shape.getSolidColor()

        //设置边框颜色
        val colorStateList = shape.getStrokeColor()
        drawable.setStroke(
            shape.strokeWidth.toInt(),
            colorStateList,
            shape.strokeDashWidth,
            shape.strokeGap
        )


        //设置渐变
        if (shape.hasGradient()) {
            drawable.setGradientCenter(shape.centerX, shape.centerY)
            if (shape.centerColor != 0) {
                drawable.colors = intArrayOf(shape.startColor, shape.centerColor, shape.endColor)
            } else {
                drawable.colors = intArrayOf(shape.startColor, shape.endColor)
            }
        }

        //渐变方向
        if (shape.gradientType == GradientDrawable.LINEAR_GRADIENT) {
            shape.gradientAngle %= 360
            if (shape.gradientAngle % 45 != 0) {
                throw XmlPullParserException(
                    "<gradient> tag requires 'angle' attribute to be a multiple of 45"
                )
            }
            var mOrientation = GradientDrawable.Orientation.LEFT_RIGHT
            when (shape.gradientAngle) {
                0 -> mOrientation = GradientDrawable.Orientation.LEFT_RIGHT
                45 -> mOrientation = GradientDrawable.Orientation.BL_TR
                90 -> mOrientation = GradientDrawable.Orientation.BOTTOM_TOP
                135 -> mOrientation = GradientDrawable.Orientation.BR_TL
                180 -> mOrientation = GradientDrawable.Orientation.RIGHT_LEFT
                225 -> mOrientation = GradientDrawable.Orientation.TR_BL
                270 -> mOrientation = GradientDrawable.Orientation.TOP_BOTTOM
                315 -> mOrientation = GradientDrawable.Orientation.TL_BR
            }
            drawable.orientation = mOrientation
        }

        if (!shape.padding.isEmpty) {
            try {
                val paddingField = drawable.javaClass.getField("mPadding")
                paddingField.isAccessible = true
                paddingField[drawable] = shape.padding
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }

        return drawable
    }


}