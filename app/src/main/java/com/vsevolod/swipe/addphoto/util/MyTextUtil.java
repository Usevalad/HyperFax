package com.vsevolod.swipe.addphoto.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;

import com.vsevolod.swipe.addphoto.R;

/**
 * Created by vsevolod on 7/17/17.
 */

public class MyTextUtil {
    public static SpannableStringBuilder toBold(String boldText, String simpleText) {
        SpannableStringBuilder sb = new SpannableStringBuilder(boldText + simpleText);
        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(b, 0, boldText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    public static Spannable toHighlight(String highlight) {
        Spannable highlighted = new SpannableString(highlight);
        highlighted.setSpan(new BackgroundColorSpan(R.color.cardview_dark_background),
                0, highlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return highlighted;
    }

}
