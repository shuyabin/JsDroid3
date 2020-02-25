package com.jsdroid.test;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.DebugUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.CloneUtils;
import com.blankj.utilcode.util.CloseUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.UiMessageUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.VibrateUtils;
import com.jsdroid.test.adapter.MainTabAdapter;
import com.jsdroid.test.widget.LogView;
import com.jsdroid.test.widget.ScriptInfoView;
import com.jsdroid.test.widget.ScriptOptionView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;

public class MainActivity extends AppCompatActivity implements UiMessageUtils.UiMessageCallback {

    private Toolbar toolBar;
    private AccountHeader accountHeader;
    private Drawer drawer;
    private QMUITabSegment tabSegment;
    private QMUIViewPager tabPage;
    private MainTabAdapter tabAdapter;
    private ScriptInfoView scriptInfoView;
    private ScriptOptionView scriptOptionTabView;
    private LogView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiMessageUtils.getInstance().addListener(this);
        setContentView(R.layout.activity_main);
        initView();
        setSupportActionBar(toolBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolBar.setElevation(10);
        }
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.ic_launcher)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withCloseOnClick(false)
                .withHeaderDivider(true)
                .withAccountHeader(accountHeader, true)
                .withToolbar(toolBar)
                .build();
        drawer.getActionBarDrawerToggle().getDrawerArrowDrawable().setColor(0xffffffff);
        JsdApp jsdApp = JsdApp.getInstance(JsdApp.class);
        drawer.addItem(new SwitchDrawerItem().withIdentifier(1).withName("悬浮窗").withChecked(jsdApp.isShowFloatView()).withSelectable(false).withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                JsdApp jsdApp = JsdApp.getInstance();
                jsdApp.switchFloatWindowState(isChecked);
            }
        }));
        drawer.addItem(new SwitchDrawerItem().withIdentifier(2).withName("音量键控制").withChecked(jsdApp.isVolumeControl()).withSelectable(false).withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                JsdApp jsdApp = JsdApp.getInstance();
                jsdApp.switchVolumeControlState(isChecked);
            }
        }));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(3).withName("重启服务").withSelectable(false).withIsExpanded(true).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                JsdApp jsdApp = JsdApp.getInstance();
                jsdApp.stopScript();
                return false;
            }
        }));

        scriptInfoView = new ScriptInfoView(this);
        scriptOptionTabView = new ScriptOptionView(this);
        logView = new LogView(this);
        tabAdapter = new MainTabAdapter(scriptInfoView, scriptOptionTabView, logView);
        tabPage.setAdapter(tabAdapter);
        tabSegment.setupWithViewPager(tabPage);
        tabPage.setSwipeable(true);
    }

    private void initView() {
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        tabSegment = (QMUITabSegment) findViewById(R.id.tabSegment);
        tabPage = (QMUIViewPager) findViewById(R.id.tabPage);

    }

    @Override
    public void handleMessage(@NonNull UiMessageUtils.UiMessage localMessage) {
        switch (localMessage.getId()) {
            case UiMessage.PRINT:
                logView.appendText(localMessage.getObject() + "\n");
                break;
            case UiMessage.LOAD_SCRIPT:
                scriptInfoView.loadHelp();
                break;
            case UiMessage.OPTION_CHANGED:
                scriptOptionTabView.loadOptions(localMessage.getObject());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UiMessageUtils.getInstance().removeListener(this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.startHomeActivity();
    }
}
