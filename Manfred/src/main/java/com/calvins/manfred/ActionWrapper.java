package com.calvins.manfred;

/**
 * Created by puppyplus on 11/3/13.
 */
public class ActionWrapper {

    private String name;
    private String category;

    public ActionWrapper(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return name;
    }

}
