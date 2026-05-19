package com.x4yi.hammersunbound.config;

import com.google.gson.JsonObject;

public class HammerMaterialData {

    public String name;
    public float baseDamage;
    public float attackSpeed;
    public int durability;

    public HammerMaterialData(String name, float baseDamage, float attackSpeed, int durability) {
        this.name = name;
        this.baseDamage = baseDamage;
        this.attackSpeed = attackSpeed;
        this.durability = durability;
    }

    public static HammerMaterialData fromJson(String name, JsonObject json) {
        float baseDamage = json.has("baseDamage") ? json.get("baseDamage").getAsFloat() : 5.0f;
        float attackSpeed = json.has("attackSpeed") ? json.get("attackSpeed").getAsFloat() : -3.2f;
        int durability = json.has("durability") ? json.get("durability").getAsInt() : 60;
        return new HammerMaterialData(name, baseDamage, attackSpeed, durability);
    }
}
