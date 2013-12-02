package com.calvins.manfred;

/**
 * Created by puppyplus on 11/3/13.
 */
public class ActionWrapper {

    private String name;
    private String event;
    private boolean major;
    private int level;
    private String stat_changes;
    private String stat_requirements;
    private String category;
    private boolean unlocked;

    public ActionWrapper(String name, String event, boolean major, int level,
        String stat_changes, String stat_requirements, String category) {
        this.name = name;
        this.category = category;
        this.level = level;
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public boolean isMajor() {
        return major;
    }

    public void setMajor(boolean major) {
        this.major = major;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int[] getStat_changes() {
        int[] changes = new int[4];
        String[] stat_changes = this.stat_changes.split("/");
        for(int i=0; i < 4; i++) {
           changes[i] = Integer.parseInt(stat_changes[i]);
        }
        return changes;
    }

    public void setStat_changes(String stat_changes) {
        this.stat_changes = stat_changes;
    }

    public String getStat_requirements() {
        return stat_requirements;
    }

    public void setStat_requirements(String stat_requirements) {
        this.stat_requirements = stat_requirements;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    @Override
    public String toString() {
        return name;
    }

}
