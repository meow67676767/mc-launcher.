package com.kdt.mcgui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import net.kdt.pojavlaunch.R;

/**
 * A big clickable glass card: icon or letter badge on top, title and subtitle at the bottom.
 * Used for the main screen shortcuts and for the loader cards.
 */
public class GlassTile extends FrameLayout {
    private final ImageView mIcon;
    private final TextView mBadge;
    private final ImageView mCheck;
    private final TextView mTitle;
    private final TextView mSubtitle;
    private int mAccent;
    private boolean mHighlighted;

    public GlassTile(@NonNull Context context) {
        this(context, null);
    }

    public GlassTile(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_glass_tile, this, true);
        mIcon = findViewById(R.id.glass_tile_icon);
        mBadge = findViewById(R.id.glass_tile_badge);
        mCheck = findViewById(R.id.glass_tile_check);
        mTitle = findViewById(R.id.glass_tile_title);
        mSubtitle = findViewById(R.id.glass_tile_subtitle);
        setClickable(true);
        setFocusable(true);

        mAccent = ContextCompat.getColor(context, R.color.accent_mods);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GlassTile);
            mAccent = a.getColor(R.styleable.GlassTile_tileAccent, mAccent);
            mHighlighted = a.getBoolean(R.styleable.GlassTile_tileHighlighted, false);
            setTitle(a.getString(R.styleable.GlassTile_tileTitle));
            setSubtitle(a.getString(R.styleable.GlassTile_tileSubtitle));
            setBadge(a.getString(R.styleable.GlassTile_tileBadge));
            int iconRes = a.getResourceId(R.styleable.GlassTile_tileIcon, 0);
            if (iconRes != 0) setIcon(ContextCompat.getDrawable(context, iconRes));
            a.recycle();
        }
        applyLook();
    }

    private void applyLook() {
        setBackground(GlassStyle.card(getContext(), mAccent, mHighlighted));
        mBadge.setBackground(GlassStyle.badge(getContext(), mAccent));
        mCheck.setVisibility(mHighlighted ? VISIBLE : GONE);
    }

    public void setTitle(@Nullable CharSequence title) {
        mTitle.setText(title);
        setContentDescription(title);
    }

    public void setSubtitle(@Nullable CharSequence subtitle) {
        mSubtitle.setText(subtitle);
        mSubtitle.setVisibility(subtitle == null || subtitle.length() == 0 ? GONE : VISIBLE);
    }

    public void setIcon(@Nullable Drawable icon) {
        mIcon.setImageDrawable(icon);
        mIcon.setVisibility(icon == null ? GONE : VISIBLE);
    }

    public void setBadge(@Nullable CharSequence badge) {
        boolean has = badge != null && badge.length() > 0;
        mBadge.setText(badge);
        mBadge.setVisibility(has ? VISIBLE : GONE);
    }

    public void setAccent(@ColorInt int accent) {
        mAccent = accent;
        applyLook();
    }

    public void setHighlighted(boolean highlighted) {
        mHighlighted = highlighted;
        applyLook();
    }
}
