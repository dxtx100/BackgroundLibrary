package com.noober.background.drawable;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

import com.noober.background.Shape;

import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by xiaoqi on 2018/9/12
 */
public class DrawableFactory {

    //获取shape属性的drawable
    public static GradientDrawable getDrawable(TypedArray typedArray) throws XmlPullParserException {
        return getDrawable(new Shape(typedArray));
    }
    //获取shape属性的drawable
    public static GradientDrawable getDrawable(Shape shape) throws XmlPullParserException {
        return (GradientDrawable) new GradientDrawableCreator(shape).create();
    }

    //获取selector属性的drawable
    public static StateListDrawable getSelectorDrawable(TypedArray typedArray, TypedArray selectorTa) throws Exception {
        return (StateListDrawable) new SelectorDrawableCreator(typedArray, selectorTa).create();
    }

    //获取selector属性的drawable
    public static StateListDrawable getButtonDrawable(TypedArray typedArray, TypedArray buttonTa) throws Exception {
        return (StateListDrawable) new ButtonDrawableCreator(typedArray, buttonTa).create();
    }

    //获取selector属性关于text的color
    public static ColorStateList getTextSelectorColor(TypedArray textTa, int defaultColor) {
        return new ColorStateCreator(textTa, defaultColor).create();
    }

}
