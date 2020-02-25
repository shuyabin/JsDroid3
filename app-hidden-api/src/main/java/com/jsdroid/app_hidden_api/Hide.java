package com.jsdroid.app_hidden_api;

import android.hardware.input.InputManager;
import android.os.Binder;
import android.view.InputDevice;
import android.view.WindowManagerPolicy;
import android.view.inputmethod.InputMethodManager;

public class Hide {
    public static void main(String[] args) {
        int[] inputDeviceIds = InputManager.getInstance().getInputDeviceIds();
        for (int deviceId : inputDeviceIds) {
            InputDevice inputDevice = InputManager.getInstance().getInputDevice(deviceId);
        }
        InputManager inputManager;
        WindowManagerPolicy.PointerEventListener pointerEventListener;

    }
}
