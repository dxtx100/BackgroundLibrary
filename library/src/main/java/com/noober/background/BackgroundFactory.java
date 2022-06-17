package com.noober.background;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.noober.background.drawable.DrawableFactory;
import com.noober.background.view.Const;

import java.lang.reflect.Constructor;
import java.util.Map;

public class BackgroundFactory implements LayoutInflater.Factory2 {

    private LayoutInflater.Factory mViewCreateFactory;
    private LayoutInflater.Factory2 mViewCreateFactory2;

    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class, AttributeSet.class};
    private static final Object[] mConstructorArgs = new Object[2];
    private static final Map<String, Constructor<? extends View>> sConstructorMap = new ArrayMap<>();

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        //转换BLView为普通的,不适应与dataBinding等
        if(Config.switchBlToOriginal)name = switchBLViewToOriginal(name);

        View view = null;
        if (mViewCreateFactory2 != null) {
            view = mViewCreateFactory2.onCreateView(name, context, attrs);
            if (view == null) {
                view = mViewCreateFactory2.onCreateView(null, name, context, attrs);
            }
        } else if (mViewCreateFactory != null) {
            view = mViewCreateFactory.onCreateView(name, context, attrs);
        }

        view = setViewBackground(name, context, attrs, view);

        //支持拦截器功能
        if (Config.onCreateViewInterceptor != null && view != null) {
            Config.onCreateViewInterceptor.invoke(view, attrs);
        }

        return view;
    }

    /**
     * 不推荐转换
     * @param name
     * @return
     */
    private String switchBLViewToOriginal(String name) {
        if(name.equals(Const.BLButton)){
            name = "Button";
        }else if(name.equals(Const.BLCheckBox)){
            name = "CheckBox";
        }else if(name.equals(Const.BLEditText)){
            name = "EditText";
        }else if(name.equals(Const.BLFrameLayout)){
            name = "FrameLayout";
//        }else if(name.equals(Const.BLGridLayout)){
//            name = "GridLayout";
//        }else if(name.equals(Const.BLGridView)){
//            name = "GridView";
//        }else if(name.equals(Const.BLImageButton)){
//            name = "ImageButton";
        }else if(name.equals(Const.BLImageView)){
            name = "ImageView";
        }else if(name.equals(Const.BLLinearLayout)){
            name = "LinearLayout";
//        }else if(name.equals(Const.BLListView)){
//            name = "ListView";
        }else if(name.equals(Const.BLRadioButton)){
            name = "RadioButton";
//        }else if(name.equals(Const.BLRadioGroup)){
//            name = "RadioGroup";
        }else if(name.equals(Const.BLRelativeLayout)){
            name = "RelativeLayout";
//        }else if(name.equals(Const.BLScrollView)){
//            name = "ScrollView";
        }else if(name.equals(Const.BLTextView)){
            name = "TextView";
        }else if(name.equals(Const.BLView)){
            name = "View";
        }
        return name;
    }

    @Nullable
    public static View setViewBackground(Context context, AttributeSet attrs, View view){
        return setViewBackground(null, context, attrs, view);
    }

    @Nullable
    private static View setViewBackground(String name, Context context, AttributeSet attrs, View view) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.background);
        TypedArray textTa = context.obtainStyledAttributes(attrs, R.styleable.text_selector);

        //pressed 以下不重要
        TypedArray selectorTa = context.obtainStyledAttributes(attrs, R.styleable.background_selector);
        TypedArray buttonTa = context.obtainStyledAttributes(attrs, R.styleable.background_button_drawable);
        TypedArray otherTa = context.obtainStyledAttributes(attrs, R.styleable.bl_other);

//        TypedArray background = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background});

        Shape shape = new Shape(typedArray);

        try {
            if (!shape.hasColor() && !shape.hasCorner() && selectorTa.getIndexCount() == 0
                    && textTa.getIndexCount() == 0 && buttonTa.getIndexCount() == 0) {
                return view;
            }
            if (view == null) {
                view = createViewFromTag(context, name, attrs);
            }
            if (view == null) {
                return null;
            }

            GradientDrawable drawable = null;
            StateListDrawable stateListDrawable = null;
            if(buttonTa.getIndexCount() > 0 && view instanceof CompoundButton){
                view.setClickable(true);
                ((CompoundButton) view).setButtonDrawable(DrawableFactory.getButtonDrawable(typedArray, buttonTa));
            } else if (selectorTa.getIndexCount() > 0) {
                stateListDrawable = DrawableFactory.getSelectorDrawable(typedArray, selectorTa);
                view.setClickable(true);
                setDrawable(stateListDrawable, view, otherTa);
            } else if(shape.hasColor()){
                drawable = DrawableFactory.getDrawable(shape);
                setDrawable(drawable, view, otherTa);
            }

            if (view instanceof TextView && textTa.getIndexCount() > 0) {
                //取默认颜色混合state
                TypedArray ta = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.textColor});
                int color = ta.getColor(0, 0);
                ta.recycle();
                ((TextView) view).setTextColor(DrawableFactory.getTextSelectorColor(textTa, color));
            }

            //水波纹
            if (typedArray.hasValue(R.styleable.background_bl_ripple_color) || Config.globalRippleEnable) {
                int color = typedArray.getColor(R.styleable.background_bl_ripple_color, Config.defaultRippleColor);
                Drawable contentDrawable = (stateListDrawable == null ? drawable : stateListDrawable);
                if (contentDrawable == null) {
                    TypedArray background = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background});
                    contentDrawable = background.getDrawable(0);
                }
                RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(color), contentDrawable, contentDrawable);
                view.setClickable(true);
                view.setBackground(rippleDrawable);
            }

            //是否裁剪,按背景裁剪,裁剪为圆形等
            if(otherTa.hasValue(R.styleable.bl_other_bl_isClip)){
                boolean isClip = otherTa.getBoolean(R.styleable.bl_other_bl_isClip, false);
                if (isClip) {
                    view.setOutlineProvider(shape.getOutlineProvider());
                    view.setClipToOutline(true);
                }
            }
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return view;
        } finally {
            typedArray.recycle();
            selectorTa.recycle();
            textTa.recycle();
            buttonTa.recycle();
            otherTa.recycle();
        }
    }

    private static void setDrawable(Drawable drawable, View view, TypedArray otherTa){

        if(view instanceof TextView){
            if(otherTa.hasValue(R.styleable.bl_other_bl_position)){
                if(otherTa.getInt(R.styleable.bl_other_bl_position, 0) == 1){
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    ((TextView)view).setCompoundDrawables(drawable, null, null, null);
                }else if(otherTa.getInt(R.styleable.bl_other_bl_position, 0) == 2){
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    ((TextView)view).setCompoundDrawables(null, drawable, null, null);
                }else if(otherTa.getInt(R.styleable.bl_other_bl_position, 0) == 4){
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    ((TextView)view).setCompoundDrawables(null, null, drawable, null);
                }else if(otherTa.getInt(R.styleable.bl_other_bl_position, 0) == 8){
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    ((TextView)view).setCompoundDrawables(null, null, null, drawable);
                }
            }else {
                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    view.setBackground(drawable);
                }else {
                    view.setBackgroundDrawable(drawable);
                }
            }
        }else {
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                view.setBackground(drawable);
            }else {
                view.setBackgroundDrawable(drawable);
            }
        }

    }

    public void setInterceptFactory(LayoutInflater.Factory factory) {
        mViewCreateFactory = factory;
    }

    public void setInterceptFactory2(LayoutInflater.Factory2 factory) {
        mViewCreateFactory2 = factory;
    }

    private static View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if(TextUtils.isEmpty(name)){
            return null;
        }
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        try {
            mConstructorArgs[0] = context;
            mConstructorArgs[1] = attrs;

            if (-1 == name.indexOf('.')) {
                View view = null;
                if ("View".equals(name)) {
                    view = createView(context, name, "android.view.");
                }
                if (view == null) {
                    view = createView(context, name, "android.widget.");
                }
                if (view == null) {
                    view = createView(context, name, "android.webkit.");
                }
                return view;
            } else {
                return createView(context, name, null);
            }
        } catch (Exception e) {
            Log.w("BackgroundLibrary", "cannot create 【" + name + "】 : ");
            return null;
        } finally {
            mConstructorArgs[0] = null;
            mConstructorArgs[1] = null;
        }
    }

    private static View createView(Context context, String name, String prefix) throws InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        try {
            if (constructor == null) {
                Class<? extends View> clazz = context.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);

                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(mConstructorArgs);
        } catch (Exception e) {
            Log.w("BackgroundLibrary", "cannot create 【" + name + "】 : ");
            return null;
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return onCreateView(name, context, attrs);
    }
}
