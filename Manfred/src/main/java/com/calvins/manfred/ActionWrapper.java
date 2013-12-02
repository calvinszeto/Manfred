package com.calvins.manfred;

import android.util.Log;

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
        this.event = event;
        this.major = major;
        this.level = level;
        this.stat_changes = stat_changes;
        this.stat_requirements = stat_requirements;
        this.category = category;
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
        for (int i = 0; i < 4; i++) {
            changes[i] = Integer.parseInt(stat_changes[i]);
        }
        return changes;
    }

    public void setStat_changes(String stat_changes) {
        this.stat_changes = stat_changes;
    }

    /*
    public String getStat_requirements() {
        return stat_requirements;
    }
    */

    public int[] getStat_requirements() {
        int[] requirements = new int[4];
        String[] stat_requirements = this.stat_requirements.split("/");
        for (int i = 0; i < 4; i++) {
            requirements[i] = (stat_requirements[i].equals("0") ? 0 :
                    Integer.parseInt(stat_requirements[i].substring(1)));
        }
        return requirements;
    }

    public boolean meetsRequirements(int weight, int vo2_max, int squat, int body_fat) {
        String[] reqs = stat_requirements.split("/");
        boolean meetsWeight = (reqs[0].equals("0") ? true :
                (reqs[0].substring(0, 1).equals("g") ? weight >= Integer.parseInt(reqs[0].substring(1)) :
                        weight <= Integer.parseInt(reqs[0].substring(1))));
        boolean meetsVo2Max= (reqs[1].equals("0") ? true :
                (reqs[1].substring(0, 1).equals("g") ? vo2_max >= Integer.parseInt(reqs[1].substring(1)) :
                        vo2_max <= Integer.parseInt(reqs[1].substring(1))));
        boolean meetsSquat = (reqs[2].equals("0") ? true :
                (reqs[2].substring(0, 1).equals("g") ? squat >= Integer.parseInt(reqs[2].substring(1)) :
                        squat <= Integer.parseInt(reqs[2].substring(1))));
        boolean meetsBodyFat= (reqs[3].equals("0") ? true :
                (reqs[3].substring(0, 1).equals("g") ? body_fat >= Integer.parseInt(reqs[3].substring(1)) :
                        body_fat <= Integer.parseInt(reqs[3].substring(1))));
        return meetsBodyFat && meetsSquat && meetsVo2Max && meetsWeight;
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
