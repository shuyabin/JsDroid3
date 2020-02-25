package com.yhao.floatwindow;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;

/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */

class Util {


    static View inflate(Context applicationContext, int layoutId) {
        LayoutInflater inflate = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflate.inflate(layoutId, null);
    }


    static int getScreenWidth(Context context) {
        return QMUIDisplayHelper.getScreenWidth(context);
    }

    static int getScreenHeight(Context context) {
        return QMUIDisplayHelper.getScreenHeight(context);
    }

    static boolean isViewVisible(View view) {
        return view.getGlobalVisibleRect(new Rect());
    }
}
