package com.jsdroid.test.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jsdroid.api.annotations.Doc;
import com.jsdroid.test.R;

import java.text.SimpleDateFormat;
import java.util.Date;

@Doc("运行日志界面")
public class LogView extends LinearLayout {
    private TextView logView;
    private ScrollView scrollView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss:SSS: ");

    public LogView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.jsd_tab_log, this);
        scrollView = findViewById(R.id.scrollView);
        logView = findViewById(R.id.logView);
        findViewById(R.id.btnClear).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setText(null);
            }
        });

    }

    public void setText(String text) {
        logView.setText(text);
    }

    public void appendText(String text) {
        String format = simpleDateFormat.format(new Date());
        logView.append(format);
        logView.append(text);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
