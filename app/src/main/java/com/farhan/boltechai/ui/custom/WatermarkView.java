package com.farhan.boltechai.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.farhan.boltechai.AppConstants;
import com.farhan.boltechai.R;

/**
 * WatermarkView — Reusable custom view that displays the developer credit.
 *
 * WATERMARK (required):
 * - Shows "Developed by Farhan Shariar" on every screen
 * - 50% opacity (alpha = 0.5)
 * - Placed in the footer area
 * - Never blocks touches: clickable=false, focusable=false
 * - importantForAccessibility = no
 * - Text comes from AppConstants.DEVELOPER_CREDIT
 * - No setting may hide it
 *
 * Lab Topic: Custom view
 */
public class WatermarkView extends LinearLayout {

    private TextView watermarkText;

    public WatermarkView(Context context) {
        super(context);
        init(context);
    }

    public WatermarkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WatermarkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Initializes the watermark: creates a small, semi-transparent,
     * centered TextView that never intercepts touch events.
     */
    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        setPadding(0, 8, 0, 8);

        // Must never block touches
        setClickable(false);
        setFocusable(false);

        watermarkText = new TextView(context);
        // Text from single constant — never allow hiding
        watermarkText.setText(AppConstants.DEVELOPER_CREDIT);
        watermarkText.setTextSize(10f);
        watermarkText.setAlpha(0.5f); // 50% opacity
        watermarkText.setGravity(Gravity.CENTER);
        watermarkText.setClickable(false);
        watermarkText.setFocusable(false);
        watermarkText.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        watermarkText.setTextColor(context.getResources().getColor(
                R.color.watermark_text, context.getTheme()));

        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        addView(watermarkText, params);
    }
}
