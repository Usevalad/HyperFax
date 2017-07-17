package com.vsevolod.swipe.addphoto.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.MyApplication;

import static com.vsevolod.swipe.addphoto.R.color.highlight;

/**
 * Created by vsevolod on 7/17/17.
 */

public class MyTextUtil {
    public static SpannableStringBuilder toBold(String boldText) {
        SpannableStringBuilder sb = new SpannableStringBuilder(boldText);
        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(b, 0, boldText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

//    private static SpannableStringBuilder toHighlight(String highlight) {
//        SpannableStringBuilder highlighted = new SpannableStringBuilder(highlight);
//        highlighted.setSpan(new BackgroundColorSpan(R.color.cardview_dark_background),
//                0, highlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return highlighted;
//    }

    public static SpannableStringBuilder highLightMatches(String text, String searchString) {
        SpannableStringBuilder highlighted = new SpannableStringBuilder(text);
        if (!TextUtils.isEmpty(searchString)) {
            String lowerCaseText = text.toLowerCase();
            String lowerCaseSearchString = searchString.toLowerCase();
            int searchStringLength = searchString.length();

            if (lowerCaseText.contains(lowerCaseSearchString)) {
                int start = lowerCaseText.indexOf(lowerCaseSearchString);
                int end = start + searchStringLength;
                highlighted.setSpan(new BackgroundColorSpan(Color.YELLOW),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return highlighted;
    }

}
