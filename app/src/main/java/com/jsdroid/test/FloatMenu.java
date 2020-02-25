package com.jsdroid.test;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.UiMessageUtils;
import com.jsdroid.test.widget.ScriptOptionView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.ViewStateListenerAdapter;

public class FloatMenu implements UiMessageUtils.UiMessageCallback {

    public static final String TAG = "jsd_float_menu";

    @Override
    public void handleMessage(@NonNull UiMessageUtils.UiMessage localMessage) {
        switch (localMessage.getId()) {
            case UiMessage.OPTION_CHANGED:
                if (scriptOptionView != null) {
                    scriptOptionView.loadOptions(localMessage.getObject());
                }
                break;
            case UiMessage.SRIPT_START:
                hide();
                FloatLogo.getInstance().show();
                break;
        }
    }

    private static class Single {
        static FloatMenu single = new FloatMenu();
    }

    public static FloatMenu getInstance() {
        return Single.single;
    }

    private JsdApp jsdApp;
    private View view;
    private ScriptOptionView scriptOptionView;

    private FloatMenu() {
    }

    public void init(final JsdApp jsdApp) {
        this.jsdApp = jsdApp;

    }

    private void initView() {
        FloatWindow.with(jsdApp)
                .setTag(TAG)
                .setMoveType(MoveType.inactive)
                .setView(R.layout.jsd_float_menu)
                .setDesktopShow(true)
                .setViewStateListener(new ViewStateListenerAdapter() {
                    @Override
                    public void onShow(final IFloatWindow floatWindow) {
                        floatWindow.removeWindowFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        toCenter(floatWindow);
                    }
                })
                .build();
        IFloatWindow floatWindow = FloatWindow.get(TAG);
        if (floatWindow != null) {
            view = floatWindow.getView();
            view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hide();
                    FloatLogo.getInstance().show();
                }
            });
            view.findViewById(R.id.btnRun).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jsdApp.startScript();
                }
            });
            scriptOptionView = new ScriptOptionView(view.getContext());
            LinearLayout content = view.findViewById(R.id.content);
            content.addView(scriptOptionView);
            floatWindow.show();
        }
        UiMessageUtils.getInstance().addListener(this);
    }


    public void show() {
        final IFloatWindow floatWindow = FloatWindow.get(TAG);
        if (floatWindow != null) {
            Log.d(TAG, "show: ");
            floatWindow.show();
//            floatWindow.removeWindowFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            toCenter(floatWindow);
        } else {
            initView();
        }

    }

    private void toCenter(final IFloatWindow floatWindow) {
        if (view != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    if (floatWindow.isShowing()) {
                        int x = QMUIDisplayHelper.getScreenWidth(jsdApp) - view.getWidth();
                        int y = QMUIDisplayHelper.getScreenHeight(jsdApp) - view.getHeight();
                        floatWindow.updateX(x / 2);
                        floatWindow.updateY(y / 2);
                    }

                }
            });
        }
    }

    public void hide() {
        if (view != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    IFloatWindow floatWindow = FloatWindow.get(TAG);
                    if (floatWindow != null) {
                        floatWindow.addWindowFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        floatWindow.hide();
                    }
                }
            });
        }

    }
}
