package com.jsdroid.runner;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 一个简单的运行su命令的工具类
 */
public class SimpleSu {

    private static final String TAG = "SimpleSu";

    private static final Object LOCK = new Object();

    private SimpleSu() {

    }

    public static void su(String command) {
        su(command, false);
    }

    public static String su(String command, boolean output) {
        synchronized (LOCK) {
            return exec(command, output);
        }
    }

    private static String exec(String command, boolean output) {
        d("[command] " + command);
        StringWriter sw = new StringWriter();
        try {
            Process process = Runtime.getRuntime().exec("su");
            Thread out = null;
            Thread err = null;
            if (output) {
                PrintWriter pw = new PrintWriter(sw);
                out = new Thread(new StreamGobbler(process.getInputStream(), pw));
                err = new Thread(new StreamGobbler(process.getErrorStream(), pw));
                out.start();
                err.start();
            }
            PrintWriter stdin = new PrintWriter(process.getOutputStream());
            stdin.println(command);
            stdin.println("exit");
            stdin.flush();
            stdin.close();
            process.waitFor();
            if (output) {
                out.join();
                err.join();
            }
            return sw.toString();
        } catch (IOException e) {
            d("io", e);
            return null;
        } catch (InterruptedException e) {
            d("interrupted", e);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public static boolean hasSu() {
        return Holder.SU;
    }

    static void d(String msg) {
        Log.d(TAG, msg);
    }

    static void d(String msg, Throwable t) {
        Log.d(TAG, msg, t);
    }

    private static class Holder {
        static final boolean SU = checkSu();

        private Holder() {

        }

        private static boolean checkSu() {
            for (String dir : System.getenv("PATH").split(":")) {
                File path = new File(dir, "su");
                try {
                    //Os.access(path.getPath(), 1)
                    if (path.exists()) {
                        d("has su: " + path);
                        return true;
                    }
                } catch (Exception e) {
                    d("Can't access " + path);
                }
            }
            d("has no su");
            return false;
        }
    }

    private static class StreamGobbler implements Runnable {
        private final InputStream is;
        private final PrintWriter pw;

        StreamGobbler(InputStream is, PrintWriter pw) {
            this.is = new BufferedInputStream(is);
            this.pw = pw;
        }

        @Override
        public void run() {
            try (
                    BufferedReader br = new BufferedReader(new InputStreamReader(is))
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    synchronized (pw) {
                        d(line);
                        pw.println(line);
                        pw.flush();
                    }
                }
            } catch (IOException e) {
                d("io", e);
            }
        }
    }

}
