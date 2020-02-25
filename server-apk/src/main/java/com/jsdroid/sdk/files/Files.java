package com.jsdroid.sdk.files;

import com.jsdroid.script.JsDroidScript;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class Files {
    private JsDroidScript script;

    public Files(JsDroidScript script) {
        this.script = script;
    }

    public InputStream openRes(String file) {
        return script.getClass().getClassLoader().getResourceAsStream(file);
    }

    public byte[] readRes(String file) {
        try (InputStream input = openRes(file)) {
            return IOUtils.toByteArray(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String readResToString(String file, String encode) {
        byte[] bytes = readRes(file);
        if (bytes == null) {
            return null;
        }
        try {
            if (encode == null) {
                return new String(bytes);
            }
            return new String(bytes, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String readResToString(String file) {
        return readResToString(file, null);
    }

    public byte[] readFile(String file) {
        try (InputStream input = new FileInputStream(file)) {
            return IOUtils.toByteArray(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String readFileToString(String file, String encode) {
        byte[] bytes = readFile(file);
        if (encode == null) {
            return new String(bytes);
        }
        try {
            return new String(bytes, encode);
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public boolean writeStringToFile(String file, String text, String encode) {
        if (encode != null) {
            try {
                return writeBytesToFile(file, text.getBytes(encode));
            } catch (UnsupportedEncodingException e) {
            }
        } else {
            return writeBytesToFile(file, text.getBytes());
        }
        return false;
    }

    public boolean writeBytesToFile(String file, byte[] bytes) {
        if (bytes == null) {
            return false;
        }

        File dist = new File(file);
        File parentFile = dist.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        try {
            FileUtils.writeByteArrayToFile(dist, bytes);
            return true;
        } catch (IOException e) {
        }
        return false;
    }
}
