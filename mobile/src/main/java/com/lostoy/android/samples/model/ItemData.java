package com.lostoy.android.samples.model;

import java.io.Serializable;
import java.util.List;

public class ItemData implements Serializable {

    public int type;
    public String name;
    public String description;
    public List<SubItem> subItems;

    public static class SubItem {
        public String name;
        public String description;

        public SubItem(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
