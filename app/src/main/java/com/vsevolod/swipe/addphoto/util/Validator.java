package com.vsevolod.swipe.addphoto.util;

import android.text.TextUtils;
import android.widget.EditText;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.constant.Constants;

/**
 * Created by Student Vsevolod on 8/14/17.
 * usevalad.uladzimiravich@gmail.com
 * <p>
 * validate text from EditText
 * if text is not valid - set error, request focus
 */

public class Validator {
    /**
     * Validating phone number from editText,
     * requesting focus and setting error if number is not valid
     *
     * @param editText - necessary to check
     * @return string from editText
     */
    public static String validatePhone(EditText editText) {
        String phoneNumber = editText.getText().toString();
        String ukraineCode = MyApplication.getContext().getString(R.string.ukraine_code);
        if (TextUtils.isEmpty(phoneNumber)) {
            editText.requestFocus();
            String error = MyApplication.getContext().getString(R.string.fill_field);
            editText.setError(error);
        } else if (!phoneNumber.startsWith(ukraineCode)) {
            String error = MyApplication.getContext().getString(R.string.number_format);
            editText.requestFocus();
            editText.setError(error);
        } else if (phoneNumber.length() > Constants.PHONE_NUMBER_LENGTH) {
            String error = MyApplication.getContext().getString(R.string.number_to_long);
            editText.requestFocus();
            editText.setError(error);
        } else if (phoneNumber.length() < Constants.PHONE_NUMBER_LENGTH) {
            String error = MyApplication.getContext().getString(R.string.number_to_short);
            editText.requestFocus();
            editText.setError(error);
        } else
            editText.setError(null);
        return phoneNumber;
    }

    /**
     * Validating text from editText
     * requesting focus and setting error if text is empty
     *
     * @param editText - necessary to check
     * @return string from editText
     */
    public static String validateInput(EditText editText) {
        String text = editText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            String error = MyApplication.getContext().getString(R.string.fill_field);
            editText.setError(error);
            editText.requestFocus();
        }
        return text;
    }
}