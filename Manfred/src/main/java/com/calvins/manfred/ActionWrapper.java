package com.calvins.manfred;

/**
 * Created by puppyplus on 11/3/13.
 */
public class ActionWrapper {

    private String name;
    private String category;
    private String path;

    public ActionWrapper(String name, String category, String path) {
        this.name = name;
        this.category = category;
        this.path = path;
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

    @Override
    public String toString() {
        return name;
    }

}
