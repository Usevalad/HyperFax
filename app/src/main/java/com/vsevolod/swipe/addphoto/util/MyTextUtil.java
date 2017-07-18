package com.vsevolod.swipe.addphoto.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;

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

    public static SpannableStringBuilder highLightMatches(String text, String searchString) {
        SpannableStringBuilder highlighted = new SpannableStringBuilder(text);
        if (!TextUtils.isEmpty(searchString)) {
            recursion(highlighted, text.toLowerCase(), searchString.toLowerCase());
        }
        return highlighted;
    }

    private static void recursion(SpannableStringBuilder highlighted, String text, String searchString) {
        int searchStringLength = searchString.length();
        if (text.contains(searchString)) {
            setSpan(highlighted, text.indexOf(searchString), searchStringLength, text.length());
            if (text.length() >= searchString.length()) {
                String subString = text.substring(text.indexOf(searchString) + searchStringLength, text.length());
                if (subString.contains(searchString)) {
                    recursion(highlighted, subString, searchString);
                }
            }
        }
    }

    private static void setSpan(SpannableStringBuilder highlighted, int start, int searchStringLength, int textLength) {
        start = highlighted.length() > textLength ? start + highlighted.length() - textLength : start;
        int end = start + searchStringLength;
        highlighted.setSpan(new BackgroundColorSpan(Color.YELLOW), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}