package com.jsdroid.api.entity;

import com.jsdroid.ipc.data.Input;
import com.jsdroid.ipc.data.IpcData;
import com.jsdroid.ipc.data.Output;

import java.io.IOException;

public class Script implements IpcData {
    private String pkg;
    private String name;
    private String version;
    private String note;

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public void write(Output output) throws IOException {
        output.write(pkg);
        output.write(name);
        output.write(version);
        output.write(note);
    }

    @Override
    public void read(Input input) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        pkg = input.read();
        name = input.read();
        version = input.read();
        note = input.read();
    }
}
