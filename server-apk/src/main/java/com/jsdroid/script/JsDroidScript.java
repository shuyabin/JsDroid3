package com.jsdroid.script;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;

import com.jsdroid.api.IInput;
import com.jsdroid.api.IJsDroidApp;
import com.jsdroid.api.annotations.FieldName;
import com.jsdroid.api.annotations.MethodDoc;
import com.jsdroid.findimg.FindImg;
import com.jsdroid.findpic.FindPic;
import com.jsdroid.sdk.apps.Apps;
import com.jsdroid.sdk.devices.Devices;
import com.jsdroid.sdk.directions.Directions;
import com.jsdroid.sdk.events.Events;
import com.jsdroid.sdk.files.Files;
import com.jsdroid.sdk.gestures.Gestures;
import com.jsdroid.sdk.https.Https;
import com.jsdroid.sdk.logs.Logs;
import com.jsdroid.sdk.nodes.Node;
import com.jsdroid.sdk.nodes.Nodes;
import com.jsdroid.sdk.nodes.Store;
import com.jsdroid.sdk.points.Points;
import com.jsdroid.sdk.rects.Rects;
import com.jsdroid.sdk.screens.Screens;
import com.jsdroid.sdk.scripts.Scripts;
import com.jsdroid.sdk.shells.Shells;
import com.jsdroid.sdk.sockets.Sockets;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import groovy.lang.Binding;
import groovy.lang.Script;

public abstract class JsDroidScript extends Script {
    private IJsDroidApp app;
    private String pkg;
    private Files files;

    public JsDroidScript() {
        files = new Files(this);
    }

    public JsDroidScript(Binding binding) {
        super(binding);
        files = new Files(this);

    }

    public Object load(String name) throws InterruptedException {
        return Scripts.getInstance(pkg).load(this, name);
    }

    public Files getJFile() {
        return files;
    }

    public Logs getJLog() {
        return Logs.getInstance();
    }

    public Https getJHttp() {
        return Https.getInstance();
    }

    public Sockets getJSocket() {
        return Sockets.getInstance();
    }

    public Devices getJDevice() {
        return Devices.getInstance();
    }

    public Directions getJDirection() {
        return Directions.getInstance();
    }

    public Events getJEvent() {
        return Events.getInstance();
    }

    public Gestures getJGesture() {
        return Gestures.getInstance();
    }

    public Nodes getJNode() {
        return Nodes.getInstance();
    }

    public Points getJPoint() {
        return Points.getInstance();
    }

    public Rects getJRect() {
        return Rects.getInstance();
    }

    public Screens getJScreen() {
        return Screens.getInstance();
    }

    public Shells getJShell() {
        return Shells.getInstance();
    }

    public Apps getJApp() {
        return Apps.getInstance(pkg);
    }

    public void setApp(String pkg, IJsDroidApp app) {
        this.pkg = pkg;
        this.app = app;
        setProperty("out", getJApp());
    }

    @MethodDoc("弹出toast")
    public void toast(Object text) {
        Apps defaultApp = Apps.getRunnerApp();
        if (defaultApp != null) {
            defaultApp.toast(text);
        }
    }

    @MethodDoc("输入文字")
    public void inputText(
            @FieldName("文字") String text) {
        Apps defaultApp = Apps.getRunnerApp();
        if (defaultApp != null) {
            try {
                IInput input = defaultApp.getInput();
                input.input(text);
            } catch (InterruptedException e) {
            }
        }
    }

    @MethodDoc("清除文字")
    public void clearInput(@FieldName("光标前位数") int before, @FieldName("光标后位数") int after) {
        Apps defaultApp = Apps.getRunnerApp();
        if (defaultApp != null) {
            try {
                IInput input = defaultApp.getInput();
                input.clear(before, after);
            } catch (InterruptedException e) {
            }
        }
    }

    @MethodDoc("输入go")
    public void inputGo() {
        Apps defaultApp = Apps.getRunnerApp();
        if (defaultApp != null) {
            try {
                IInput input = defaultApp.getInput();
                input.inputGo();
            } catch (InterruptedException e) {
            }
        }
    }

    @MethodDoc("输入结束")
    public void inputDone() {
        Apps defaultApp = Apps.getRunnerApp();
        if (defaultApp != null) {
            try {
                IInput input = defaultApp.getInput();
                input.inputDone();
            } catch (InterruptedException e) {
            }
        }
    }

    @MethodDoc("输入下一步")
    public void inputNext() {
        Apps defaultApp = Apps.getRunnerApp();
        if (defaultApp != null) {
            try {
                IInput input = defaultApp.getInput();
                input.inputNext();
            } catch (InterruptedException e) {
            }
        }
    }

    @MethodDoc("输入搜索动作")
    public void inputSearch() {
        Apps defaultApp = Apps.getRunnerApp();
        if (defaultApp != null) {
            try {
                IInput input = defaultApp.getInput();
                input.inputSearch();
            } catch (InterruptedException e) {
            }
        }
    }

    @MethodDoc("输入发送动作")
    public void inputSend() {
        Apps defaultApp = Apps.getRunnerApp();
        if (defaultApp != null) {
            try {
                IInput input = defaultApp.getInput();
                input.inputSend();
            } catch (InterruptedException e) {
            }
        }
    }

    @MethodDoc("输入未指定的动作")
    public void inputUnspecified() {
        Apps defaultApp = Apps.getRunnerApp();
        if (defaultApp != null) {
            try {
                IInput input = defaultApp.getInput();
                input.inputUnspecified();
            } catch (InterruptedException e) {
            }
        }
    }

    @MethodDoc("手指按下")
    public void touchDown(@FieldName("x") int x, @FieldName("y") int y) {
        getJEvent().touchDown(x, y);
    }

    @MethodDoc("手指弹起")
    public void touchUp(int x, int y) {
        getJEvent().touchUp(x, y);
    }

    @MethodDoc("手指移动")
    public void touchMove(@FieldName("x") int x, @FieldName("y") int y) {
        getJEvent().touchMove(x, y);
    }

    @MethodDoc("点击屏幕")
    public void click(@FieldName("x") int x, @FieldName("y") int y) {
        getJEvent().tap(x, y);
    }

    @MethodDoc("点击屏幕")
    public void tap(@FieldName("x") int x, @FieldName("y") int y) {
        getJEvent().tap(x, y);
    }

    @MethodDoc("手指滑动")
    public void swipe(@FieldName("x1") int x1, @FieldName("y1") int y1, @FieldName("x2") int x2,
                      @FieldName("y2") int y2) {
        getJEvent().swipe(x1, y1, x2, y2, 20);
    }

    @MethodDoc("手指滑动")
    public void swipe(@FieldName("x1") int x1, @FieldName("y1") int y1, @FieldName("x2") int x2,
                      @FieldName("y2") int y2, @FieldName("补间数量") int steps) {
        getJEvent().swipe(x1, y1, x2, y2, steps);
    }

    @MethodDoc("查找单个节点")
    public Node findNode(@FieldName("正则表达式") Pattern pattern) {
        final Store<Node> nodeStore = new Store<>();
        getJNode().eachNode(new Node.NodeEach() {
            @Override
            public boolean each(Node node) {
                try {
                    if (pattern.matcher(node.getText()).matches()) {
                        nodeStore.set(node);
                        return true;
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getRes()).matches()) {
                        nodeStore.set(node);
                        return true;
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getDesc()).matches()) {
                        nodeStore.set(node);
                        return true;
                    }
                } catch (Exception e) {
                }
                return false;
            }
        });
        return nodeStore.get();

    }

    @MethodDoc("查找所有节点")
    public List<Node> findNodeAll(@FieldName("正则表达式") Pattern pattern) {
        final List<Node> nodes = new ArrayList<>();
        getJNode().eachNode(new Node.NodeEach() {
            @Override
            public boolean each(Node node) {
                try {
                    if (pattern.matcher(node.getText()).matches()) {
                        nodes.add(node);
                        return false;
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getRes()).matches()) {
                        nodes.add(node);
                        return false;
                    }
                } catch (Exception e) {
                }
                try {
                    if (pattern.matcher(node.getDesc()).matches()) {
                        nodes.add(node);
                        return false;
                    }
                } catch (Exception e) {
                }
                return false;
            }
        });
        return nodes;
    }

    @MethodDoc("高级找图")
    public FindImg.Rect findImg(@FieldName("png文件路径") String pngFile,
                                @FieldName("左") int left,
                                @FieldName("上") int top,
                                @FieldName("右") int right,
                                @FieldName("下") int bottom,
                                @FieldName("色差") int offset,
                                @FieldName("相似度") float sim) {
        Bitmap image;
        try {
            image = BitmapFactory.decodeStream(getJFile().openRes(pngFile));
        } catch (Exception e) {
            return null;
        }
        return findImg(image,
                left,
                top,
                right,
                bottom,
                offset,
                sim);
    }

    @MethodDoc("高级找图")
    public FindImg.Rect findImg(@FieldName("内存图片") Bitmap image,
                                @FieldName("左") int left,
                                @FieldName("上") int top,
                                @FieldName("右") int right,
                                @FieldName("下") int bottom,
                                @FieldName("色差") int offset,
                                @FieldName("相似度") float sim) {
        Bitmap screen;
        try {
            screen = getJScreen().capture();
        } catch (InterruptedException e) {
            return null;
        }
        return findImg(screen,
                image,
                left,
                top,
                right,
                bottom,
                offset,
                sim);
    }

    @MethodDoc("高级找图")
    public FindImg.Rect findImg(@FieldName("被找内存图") Bitmap screen,
                                @FieldName("要找内存图") Bitmap image,
                                @FieldName("做") int left,
                                @FieldName("上") int top,
                                @FieldName("右") int right,
                                @FieldName("下") int bottom,
                                @FieldName("色差") int offset,
                                @FieldName("相似度") float sim) {
        int distance = 1;
        int level = 8;
        if (sim > 0.7) {
            distance = 2;
            level = 16;
        }
        if (image == null) {
            return null;
        }
        return FindImg.findImg(screen,
                image,
                level,
                left,
                top,
                right,
                bottom,
                offset,
                distance,
                sim);
    }


    @MethodDoc("普通找图")
    public Point findPic(@FieldName("png文件") String pngFile,
                         @FieldName("左") int left,
                         @FieldName("上") int top,
                         @FieldName("右") int right,
                         @FieldName("下") int bottom,
                         @FieldName("色差") int offset,
                         @FieldName("相似度") float sim) {
        Bitmap image;
        try {
            image = BitmapFactory.decodeStream(getJFile().openRes(pngFile));
        } catch (Exception e) {
            return null;
        }
        return findPic(image,
                (int) left,
                (int) top,
                (int) right,
                (int) bottom,
                (int) offset,
                (float) sim);
    }

    @MethodDoc("普通找图")
    public Point findPic(@FieldName("内存图") Bitmap image,
                         @FieldName("左") int left,
                         @FieldName("上") int top,
                         @FieldName("右") int right,
                         @FieldName("下") int bottom,
                         @FieldName("色差") int offset,
                         @FieldName("相似度") float sim) {
        Bitmap screen;
        try {
            screen = getJScreen().capture();
        } catch (InterruptedException e) {
            return null;
        }
        return findPic(screen,
                image,
                (int) left,
                (int) top,
                (int) right,
                (int) bottom,
                (int) offset,
                (float) sim);
    }

    @MethodDoc("普通找图")
    public Point findPic(@FieldName("被找内存图") Bitmap screen,
                         @FieldName("要找内存图") Bitmap image,
                         @FieldName("左") int left,
                         @FieldName("上") int top,
                         @FieldName("右") int right,
                         @FieldName("下") int bottom,
                         @FieldName("色差") int offset,
                         @FieldName("相似度") float sim) {
        try {
            if (image == null) {
                return null;
            }
            return FindPic.findPic(screen,
                    image,
                    (int) left,
                    (int) top,
                    (int) right,
                    (int) bottom,
                    (int) offset,
                    (float) sim);
        } catch (Exception e) {
            return null;
        }
    }


    @MethodDoc("图片转bytes")
    public byte[] bitmapToJpg(@FieldName("内存图") Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                80,
                out);
        return out.toByteArray();
    }

    @MethodDoc("图片转bytes")
    public byte[] bitmapToPng(@FieldName("内存图") Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,
                80,
                out);
        return out.toByteArray();
    }

    @MethodDoc("时间戳")
    public long time() {
        return System.currentTimeMillis();
    }

    @MethodDoc("安卓版本号")
    public int getSdk() {
        return Build.VERSION.SDK_INT;
    }

    @MethodDoc("读取配置")
    public String readConfig(@FieldName("key") String key,
                             @FieldName("默认值") String defaultValue) {
        return getJApp().readConfig(key, defaultValue);
    }

    @MethodDoc("读取配置")
    public String readConfig(@FieldName("key") String key) {
        return readConfig(key, null);
    }
}
