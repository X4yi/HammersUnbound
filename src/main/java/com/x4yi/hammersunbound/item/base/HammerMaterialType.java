package com.x4yi.hammersunbound.item.base;

public enum HammerMaterialType {

    WOOD("wood"),
    STONE("stone"),
    IRON("iron"),
    GOLD("gold"),
    DIAMOND("diamond");

    private final String name;

    HammerMaterialType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HammerMaterialType fromName(String name) {
        for (HammerMaterialType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return WOOD;
    }
}
