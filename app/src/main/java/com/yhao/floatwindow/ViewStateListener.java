package com.yhao.floatwindow;

/**
 * Created by yhao on 2018/5/5
 * https://github.com/yhaolpz
 */
public interface ViewStateListener {
    void onPositionUpdate(IFloatWindow floatWindow, int x, int y);

    void onShow(IFloatWindow floatWindow);

    void onHide(IFloatWindow floatWindow);

    void onDismiss(IFloatWindow floatWindow);

    void onMoveAnimStart(IFloatWindow floatWindow);

    void onMoveAnimEnd(IFloatWindow floatWindow);

    void onBackToDesktop(IFloatWindow floatWindow);

    void onTouchDown(IFloatWindow floatWindow);

    void onTouchMoving(IFloatWindow floatWindow);

    void onTouchUp(IFloatWindow floatWindow);

    void onTouchOutside(IFloatWindow floatWindow);
}
