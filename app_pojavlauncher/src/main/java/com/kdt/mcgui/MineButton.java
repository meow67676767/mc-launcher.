package com.kdt.mcgui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.core.content.res.ResourcesCompat;

import net.kdt.pojavlaunch.R;

/**
 * Launcher button in the glass style. By default it is a translucent glass button,
 * with app:cta="true" it becomes the orange gradient call-to-action button.
 * Text size, font and all-caps set in XML take priority over the defaults here.
 */
public class MineButton extends androidx.appcompat.widget.AppCompatButton {
	public MineButton(Context ctx) {
		this(ctx, null);
	}

	public MineButton(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init(ctx, attrs);
	}

	private void init(Context ctx, AttributeSet attrs) {
		boolean cta = false;
		boolean hasTextSize = false, hasFont = false, hasAllCaps = false;
		if (attrs != null) {
			TypedArray cs = ctx.obtainStyledAttributes(attrs, R.styleable.MineButton);
			cta = cs.getBoolean(R.styleable.MineButton_cta, false);
			cs.recycle();
			String ns = "http://schemas.android.com/apk/res/android";
			hasTextSize = attrs.getAttributeValue(ns, "textSize") != null;
			hasFont = attrs.getAttributeValue(ns, "fontFamily") != null;
			hasAllCaps = attrs.getAttributeValue(ns, "textAllCaps") != null;
		}

		setBackgroundResource(cta ? R.drawable.cta_background : R.drawable.glass_button);
		setTextColor(ResourcesCompat.getColor(getResources(),
				cta ? R.color.cta_text : R.color.glass_text, ctx.getTheme()));
		if (!hasFont) setTypeface(ResourcesCompat.getFont(ctx, R.font.manrope_bold));
		if (!hasTextSize) setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen._13ssp));
		if (!hasAllCaps) setAllCaps(false);
	}

}
