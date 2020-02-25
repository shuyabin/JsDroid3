package com.jsdroid.test.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.UiMessageUtils;
import com.jsdroid.api.annotations.Doc;
import com.jsdroid.test.JsdApp;
import com.jsdroid.test.MainActivity;
import com.jsdroid.test.R;
import com.jsdroid.test.UiMessage;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Doc("脚本配置界面")
public class ScriptOptionView extends LinearLayout {
    LinearLayout contentView;

    public ScriptOptionView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.jsd_tab_script_option, this);
        contentView = findViewById(R.id.content);
        loadOptions(null);
    }

    public void loadOptions(Object obj) {
        if (equals(obj)) {
            return;
        }
        contentView.removeAllViews();
        //将配置解析出来
        JsdApp jsd = JsdApp.getInstance();
        File scriptDir = jsd.getScriptDir();
        File optionFile = new File(scriptDir, "option.json");
        try {
            JSONObject jsonObject = JSON.parseObject(FileUtils.readFileToString(optionFile));
            for (String key : jsonObject.keySet()) {
                //添加
                JSONObject optionItem = jsonObject.getJSONObject(key);
                String name = optionItem.getString("name");
                String value = optionItem.getString("value");
                addOption(key, name, value);
            }
        } catch (IOException e) {
        }
    }

    void addOption(final String key, String name, String defaultValue) {
        MaterialEditText optionView = new MaterialEditText(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(-1, -2);
        int dp10 = QMUIDisplayHelper.dp2px(getContext(), 10);
        params.setMargins(dp10, dp10, dp10, dp10);
        optionView.setLayoutParams(params);
        optionView.setHint(name);
        final JsdApp jsdApp = JsdApp.getInstance();
        String value = jsdApp.readConfig(key, defaultValue);
        optionView.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NORMAL);
        optionView.setFloatingLabelText(name);
        optionView.setText(value);
        optionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                jsdApp.saveConfig(key, text.toString());
                UiMessageUtils.getInstance().send(UiMessage.OPTION_CHANGED, ScriptOptionView.this);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        contentView.addView(optionView);
    }
}