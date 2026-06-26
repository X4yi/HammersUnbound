package com.x4yi.hammersunbound.config;
import com.google.gson.JsonObject;
public class HammerMaterialData {
    public String name;
    public float baseDamage;
    public float attackSpeed;
    public int durability;
    public int skillCooldown;
    public HammerMaterialData(String name, float baseDamage, float attackSpeed, int durability, int skillCooldown) {
        this.name = name;
        this.baseDamage = baseDamage;
        this.attackSpeed = attackSpeed;
        this.durability = durability;
        this.skillCooldown = skillCooldown;
    }
    public static HammerMaterialData fromJson(String name, JsonObject json) {
        float baseDamage = json.has("baseDamage") ? json.get("baseDamage").getAsFloat() : 5.0f;
        float attackSpeed = fromConfigAttackSpeed(json.has("attackSpeed") ? json.get("attackSpeed").getAsFloat() : 0.8f);
        int durability = json.has("durability") ? json.get("durability").getAsInt() : 60;
        int defaultCooldown = 100; // 5s
        if (name.equalsIgnoreCase("stone")) defaultCooldown = 120; // 6s
        else if (name.equalsIgnoreCase("iron")) defaultCooldown = 140; // 7s
        else if (name.equalsIgnoreCase("gold")) defaultCooldown = 80;  // 4s
        else if (name.equalsIgnoreCase("diamond")) defaultCooldown = 160; // 8s
        int skillCooldown = json.has("skillCooldownSeconds")
                ? secondsToTicks(json.get("skillCooldownSeconds").getAsFloat())
                : (json.has("skillCooldown") ? json.get("skillCooldown").getAsInt() : defaultCooldown);
        return new HammerMaterialData(name, baseDamage, attackSpeed, durability, skillCooldown);
    }
    public static float toConfigAttackSpeed(float attributeSpeed) {
        return attributeSpeed + 4.0f;
    }
    public static float fromConfigAttackSpeed(float configSpeed) {
        return configSpeed >= 0.0f ? configSpeed - 4.0f : configSpeed;
    }
    public static int secondsToTicks(float seconds) {
        return Math.max(0, Math.round(seconds * 20.0f));
    }
    public static float ticksToSeconds(int ticks) {
        return ticks / 20.0f;
    }
}