package com.jsdroid.sdk.scripts;

public class ScriptInfo {
    private String name;
    private String version;
    private String pkg;
    private String mainScript;
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

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getMainScript() {
        return mainScript;
    }

    public void setMainScript(String mainScript) {
        this.mainScript = mainScript;
    }
}
