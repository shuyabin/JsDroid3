package com.jsdroid.test.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jsdroid.api.annotations.Doc;
import com.jsdroid.test.JsdApp;

import java.io.File;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;

@Doc("脚本信息界面")
public class ScriptInfoView extends LinearLayout {
    private MarkdownView docView;

    public ScriptInfoView(Context context) {
        super(context);
        try {
            docView = new MarkdownView(getFixedContext(getContext()));
            addView(docView, new LayoutParams(-1, -1));
            Github style = new Github();
            style.addRule("body", "line-height: 1.6", "padding: 0px");
            docView.addStyleSheet(style);
            loadHelp();
        } catch (Exception e) {
            TextView textView = new TextView(getContext());
            textView.setText("打开帮助界面失败：当前系统不支持WebView！");
            textView.setTextColor(Color.RED);
            textView.setPadding(20, 20, 20, 20);
            addView(textView);
        }
    }


    public void loadHelp() {
        if (docView == null) {
            return;
        }
        JsdApp jsdApp = JsdApp.getInstance();
        docView.loadMarkdownFromFile(new File(jsdApp.getScriptDir(), "readme.md"));
    }

    public static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) // Android Lollipop 5.0 & 5.1
            return context.createConfigurationContext(new Configuration());
        return context;
    }
}