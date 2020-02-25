package com.jsdroid.test;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.UiMessageUtils;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListenerAdapter;

/**
 * 实现启动靠右贴边
 */
public class FloatLogo implements UiMessageUtils.UiMessageCallback {
    private static FloatLogo instance = new FloatLogo();
    private JsdApp jsdApp;
    private boolean running;

    public static FloatLogo getInstance() {
        return instance;
    }

    public void init(final JsdApp jsdApp) {
        this.jsdApp = jsdApp;
    }

    private void initView() {
        FloatWindow.with(jsdApp).setMoveType(MoveType.slide)
                .setDesktopShow(true)
                .setY(Screen.height, 0.5f)
                .setX(Screen.width, 0.6f)
                .setView(R.layout.jsd_float_logo)
                .setMoveStyle(300, new AccelerateInterpolator())
                .setViewStateListener(new MyViewStateListener())
                .build();
        FloatWindow.get().getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (large) {
                    if (running) {
                        stopScript();
                    } else {
                        showConfig();
                    }
                } else {
                    showLarge(100);
                }
            }
        });
        show();
        showLarge(100);
        UiMessageUtils.getInstance().addListener(this);
    }

    private void stopScript() {
        jsdApp.stopScript();
    }

    private void showConfig() {
        FloatMenu.getInstance().show();
        hide();
    }

    public void hide() {
        IFloatWindow floatWindow = FloatWindow.get();
        if (floatWindow != null) {
            floatWindow.hide();
        }
    }

    public void show() {
        IFloatWindow floatWindow = FloatWindow.get();
        if (floatWindow == null) {
            initView();
        } else {
            floatWindow.show();
        }
    }

    public void onConfigureChanged() {
        show();
        showSmall(300);
    }

    public void setRunning(boolean running) {
        this.running = running;
        View view = FloatWindow.get().getView();
        QMUIRadiusImageView imageView = view.findViewById(R.id.logo);
        if (running) {
            imageView.setImageDrawable(jsdApp.getResources().getDrawable(R.drawable.jsd_stop));
        } else {
            imageView.setImageDrawable(jsdApp.getResources().getDrawable(R.mipmap.ic_launcher));
        }
    }

    private SlideRunnable slideRunnable;

    private void slideToSide(long time) {
        slideToSide(time, false);
    }

    private synchronized void cancelSlide() {
        if (slideRunnable != null) {
            slideRunnable.cancel = true;
        }
    }

    public synchronized void slideToSide(long time, boolean showLarge) {
        if (slideRunnable != null) {
            slideRunnable.cancel = true;
        }
        slideRunnable = new SlideRunnable();
        slideRunnable.showLarge = showLarge;
        new Handler().postDelayed(slideRunnable, time);
    }

    @Override
    public void handleMessage(@NonNull UiMessageUtils.UiMessage localMessage) {
        switch (localMessage.getId()) {
            case UiMessage.SRIPT_START:
                setRunning(true);
                break;
            case UiMessage.SRIPT_STOP:
                setRunning(false);
                break;
        }
    }


    class SlideRunnable implements Runnable {
        boolean cancel;
        boolean showLarge;

        private void slideToSide() {
            final IFloatWindow floatWindow = FloatWindow.get();
            if (floatWindow != null) {
                final View view = floatWindow.getView();
                if (view != null) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            int width = view.getWidth();
                            int height = view.getHeight();
                            int screenWidth = QMUIDisplayHelper.getScreenWidth(jsdApp);
                            int screenHeight = QMUIDisplayHelper.getScreenHeight(jsdApp);
                            int statusHeight = QMUIDisplayHelper.getStatusBarHeight(jsdApp);
                            int x = floatWindow.getX();
                            int y = floatWindow.getY();

                            int startX = x;
                            int startY = y;
                            if (x > (screenWidth - width) / 2) {
                                if (showLarge) {

                                    x = screenWidth - width;
                                } else {
                                    x = screenWidth - width / 2;
                                }
                            } else {
                                if (showLarge) {

                                    x = 0;
                                } else {
                                    x = -width / 2;
                                }
                            }
                            if (y < 0) {
                                y = 0;
                            }
                            if (y > screenHeight - height - statusHeight) {
                                y = screenHeight - height - statusHeight;
                            }
                            animatorX(startX, x, 200);
                            animatorY(startY, y, 200);
                            large = showLarge;
                            if (showLarge) {
                                view.setAlpha(1.0f);
                                showSmall(3000);
                            } else {
                                view.setAlpha(0.2f);
                            }
                        }
                    });

                }
            }
        }

        @Override
        public void run() {
            if (!cancel) {
                slideToSide();
            }
        }
    }

    ValueAnimator xAnimator;

    private void animatorX(int startX, int endX, int duration) {
        if (xAnimator != null) {
            if (xAnimator.isRunning()) {
                xAnimator.cancel();
            }
        }
        xAnimator = ObjectAnimator.ofInt(startX, endX);
        xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue();
                FloatWindow.get().updateX(x);
            }
        });
        xAnimator.setDuration(duration).start();

    }

    ValueAnimator yAnimator;

    private void animatorY(int startY, int endY, int duration) {
        if (yAnimator != null) {
            if (yAnimator.isRunning()) {
                yAnimator.cancel();
            }
        }
        yAnimator = ObjectAnimator.ofInt(startY, endY);
        yAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int y = (int) animation.getAnimatedValue();
                FloatWindow.get().updateY(y);
            }
        });
        yAnimator.setDuration(duration).start();
    }

    class MyViewStateListener extends ViewStateListenerAdapter {
        @Override
        public void onShow(IFloatWindow floatWindow) {
            floatWindow.addWindowFlag(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
            floatWindow.addWindowFlag(WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE);
        }

        @Override
        public void onTouchOutside(IFloatWindow floatWindow) {
            showSmall(0);
        }

        @Override
        public void onMoveAnimEnd(IFloatWindow floatWindow) {
            showSmall(3000);
        }

        @Override
        public void onTouchDown(IFloatWindow floatWindow) {
            cancelSlide();
        }

        @Override
        public void onTouchMoving(IFloatWindow floatWindow) {
            cancelSlide();
        }

        @Override
        public void onTouchUp(IFloatWindow floatWindow) {

        }
    }

    boolean large;

    private void showLarge(int delay) {

        slideToSide(delay, true);
    }

    private void showSmall(int delay) {
        slideToSide(delay);
    }
}
