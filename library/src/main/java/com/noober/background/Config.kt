package com.noober.background

import android.util.AttributeSet
import android.view.View

/**
 * 描述：
 * <p>
 * author: Tiger
 * date: 2022/6/14
 * version 1.0
 */
object Config {

    @JvmField
    var switchBlToOriginal = false

    /**
     * 全局水波纹
     */
    @JvmField
    var globalRippleEnable = false

    /**
     * 默认水波纹颜色,如果[全局水波纹]开了,name这个颜色就有用
     */
    @JvmField
    var defaultRippleColor = 0

    /**
     * view创建拦截器
     */
    @JvmField
    var onCreateViewInterceptor: (View.(AttributeSet) -> Unit)? = null
}