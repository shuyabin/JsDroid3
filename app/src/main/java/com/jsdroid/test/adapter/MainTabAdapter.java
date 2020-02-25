package com.jsdroid.test.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qmuiteam.qmui.widget.QMUIPagerAdapter;

public class MainTabAdapter extends QMUIPagerAdapter {
    private View[] views;

    public MainTabAdapter(View... views) {
        this.views = views;
    }

    @NonNull
    @Override
    protected Object hydrate(@NonNull ViewGroup container, int position) {

        return views[position];
    }

    @Override
    protected void populate(@NonNull ViewGroup container, @NonNull Object item, int position) {
        container.addView(views[position]);
    }

    @Override
    protected void destroy(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views[position]);
    }

    @Override
    public int getCount() {
        return views == null ? 0 : views.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "帮助";
            case 1:
                return "配置";
            case 2:
                return "日志";
        }
        return super.getPageTitle(position);
    }
}
