package com.calvins.manfred;

/**
 * Created by puppyplus on 11/3/13.
 */
public class ActionWrapper {

    private String name;
    private String category;
    private String path;
    private String event;

    public ActionWrapper(String name, String category, String path, String event) {
        this.name = name;
        this.category = category;
        this.path = path;
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPath() {
        return path;
    }

    public String getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return name;
    }

}
