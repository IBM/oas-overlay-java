package org.crshnburn.overlay;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Overlay {

    public String overlay;
    public Info info;
    private String extendsStr;
    public List<Action> actions;

    @JsonGetter("extends")
    public String getExtends() {
        return extendsStr;
    }

    @JsonSetter("extends")
    public void setExtends(String extendsStr) {
        this.extendsStr = extendsStr;
    }
}
