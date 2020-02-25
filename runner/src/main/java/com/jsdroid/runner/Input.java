package com.jsdroid.runner;

import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.EditorInfo;

import com.jsdroid.api.IInput;
import com.jsdroid.ipc.call.ServiceProxy;

public class Input extends InputMethodService implements IInput {
    private static Input instance;

    public static Input getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public void clear(int before, int after) {
        getCurrentInputConnection().deleteSurroundingText(before, after);
    }

    public void input(String text) {
        getCurrentInputConnection().commitText(text, 0);
    }

    public void performAction(int action) {
        getCurrentInputConnection().performEditorAction(action);
    }

    public void inputGo() {
        performAction(EditorInfo.IME_ACTION_GO);
    }

    public void inputDone() {
        performAction(EditorInfo.IME_ACTION_DONE);
    }

    public void inputNext() {
        performAction(EditorInfo.IME_ACTION_NEXT);
    }

    public void inputSearch() {
        performAction(EditorInfo.IME_ACTION_SEARCH);
    }

    public void inputSend() {
        performAction(EditorInfo.IME_ACTION_SEND);
    }

    public void inputUnspecified() {
        performAction(EditorInfo.IME_ACTION_UNSPECIFIED);
    }


    @Override
    public void onAddService(String serviceId, ServiceProxy serviceProxy) {

    }
}
