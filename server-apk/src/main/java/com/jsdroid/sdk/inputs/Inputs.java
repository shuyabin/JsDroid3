package com.jsdroid.sdk.inputs;

import com.jsdroid.app_hidden_api.InputUtil;

import java.util.Stack;

public class Inputs {
    private static class Single {
        static Inputs single = new Inputs();
    }

    public static Inputs getInstance() {

        return Single.single;
    }

    private Stack<String> stack = new Stack<>();

    private Inputs() {

    }

    public void openInputMethod() {
        String ime_id = System.getenv("ime_id");
        if (ime_id != null) {
            InputUtil.setInputMethod(ime_id);
        }
    }

    public void closeInputMethod() {
        openInputMethod();
        InputUtil.switchToLastInputMethod();
    }


    public synchronized void onScriptStart(String pkg) {
        if (stack.isEmpty()) {
            openInputMethod();
        }
        stack.push(pkg);
    }

    public synchronized void onScriptStop(String pkg) {
        try {
            stack.pop();
        } catch (Exception e) {
        }
        if (stack.isEmpty()) {
            closeInputMethod();
        }
    }


}
