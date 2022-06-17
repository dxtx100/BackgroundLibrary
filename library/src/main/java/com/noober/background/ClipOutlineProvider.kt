package com.noober.background

import android.graphics.Outline
import android.graphics.Path
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi

/**
 * 描述：
 * <p>
 * author: Tiger
 * date: 2022/6/14
 * version 1.0
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
object ClipOutlineProvider {


    @JvmStatic
    fun from(shape: Shape): ViewOutlineProvider {
        if (shape.needCustomClip) {
            //radius只要取其中一半数值就够了
            val r = shape.cornerRadius
            Log.d("dxtx", "裁剪->${r.toList()}")
            return ViewOutlineProvider.BACKGROUND
        } else {
            return object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    if (shape.shape == 2) {
                        outline.setOval(0, 0, view.width, view.height)
                    } else {
                        outline.setRoundRect(0, 0, view.width, view.height, shape.cornerRadius[0])
                    }
//                    outline.alpha = 0f
                }
            }
        }

    }

    private fun makePath(
        r: FloatArray,
        w: Float,
        h: Float
    ): Path {
        val path = Path()
        path.reset()
        path.moveTo(r[0], 0f)
        _p(r[0], 0f)
        path.lineTo(w - r[1], 0f)
        _p(w - r[1], 0f)
        path.quadTo(w, 0f, w, r[1])
        _p(w, 0f, w, r[1])
        path.lineTo(w, h - r[2])
        _p(w, h - r[2])
        path.quadTo(w, h, w - r[1], h)
        _p(w, h, w - r[1], h)
        path.lineTo(r[3], h)
        _p(r[3], h)
        path.quadTo(0f, h, 0f, h - r[3])
        _p(0f, h, 0f, h - r[3])
        path.lineTo(0f, r[0])
        _p(0f, r[0])
        path.quadTo(0f, 0f, r[0], 0f)
        _p(0f, 0f, r[0], 0f)
        path.close()
        Log.d("dxtx", "path.isConvex->${path.isConvex}")
        return path
    }

    private fun _p(vararg p: Float) {
        Log.d("dxtx", "路径->${p.toList()}")
    }

    private fun getProvider(block: (View) -> Path): ViewOutlineProvider {
        return object : ViewOutlineProvider() {

            override fun getOutline(view: View, outline: Outline) {
                outline.setConvexPath(block(view))
            }
        }
    }
}