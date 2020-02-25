package com.jsdroid.app_hidden_api;


import android.view.inputmethod.InputMethodManager;

public class InputUtil {
    public static void setInputMethod(String id) {
        try {
            InputMethodManager.getInstance().setInputMethod(null, id);
        } catch (Exception e) {
        }
    }

    public static void switchToLastInputMethod() {
        try {
            InputMethodManager.getInstance().switchToLastInputMethod(null);
        } catch (Exception e) {
        }
    }
}
