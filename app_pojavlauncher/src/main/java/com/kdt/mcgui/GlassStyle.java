package com.kdt.mcgui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import net.kdt.pojavlaunch.R;

/** Builds the glass-style drawables that depend on an accent color picked at runtime. */
public final class GlassStyle {
    /** Opacity of the accent at the start of the card gradient */
    private static final int ACCENT_ALPHA = 0x55;
    /** Opacity of the accent at the start of the card gradient when highlighted */
    private static final int ACCENT_ALPHA_HIGHLIGHTED = 0x88;

    private GlassStyle() {}

    /** A glass card tinted with the accent color, with a ripple on press. */
    public static Drawable card(Context context, @ColorInt int accent, boolean highlighted) {
        Resources res = context.getResources();
        int faint = ContextCompat.getColor(context, R.color.glass_fill_faint);
        GradientDrawable shape = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ColorUtils.setAlphaComponent(accent,
                        highlighted ? ACCENT_ALPHA_HIGHLIGHTED : ACCENT_ALPHA), faint});
        shape.setCornerRadius(res.getDimension(R.dimen.glass_radius));
        if (highlighted) {
            shape.setStroke(res.getDimensionPixelSize(R.dimen.glass_stroke_width_selected),
                    ContextCompat.getColor(context, R.color.cta_start));
        } else {
            shape.setStroke(res.getDimensionPixelSize(R.dimen.glass_stroke_width),
                    ContextCompat.getColor(context, R.color.glass_stroke));
        }
        return new RippleDrawable(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.glass_ripple)), shape, null);
    }

    /** A solid rounded square filled with the accent color, used as a letter badge. */
    public static Drawable badge(Context context, @ColorInt int accent) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(accent);
        shape.setCornerRadius(context.getResources().getDimension(R.dimen.glass_radius_button) * 0.85f);
        return shape;
    }
}
