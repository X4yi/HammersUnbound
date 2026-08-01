package com.x4yi.hammersunbound.crafting;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.x4yi.hammersunbound.HammersUnbound;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class HammerRecipeManager {
    private static final List<HammerForgeRecipe> RECIPES = new ArrayList<>();
    public static void loadRecipes() {
        RECIPES.clear();
        ResourceLocation recipesFile = new ResourceLocation(HammersUnbound.MODID, "hammer_recipes/recipes.json");
        try {
            InputStream stream = HammerRecipeManager.class.getClassLoader().getResourceAsStream("assets/" + recipesFile.getResourceDomain() + "/" + recipesFile.getResourcePath());
            if (stream == null) {
                stream = HammerRecipeManager.class.getResourceAsStream("/assets/" + recipesFile.getResourceDomain() + "/" + recipesFile.getResourcePath());
            }
            if (stream == null) {
                System.err.println("[HammersUnbound] Could not find hammer_recipes/recipes.json");
                return;
            }
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(new InputStreamReader(stream, StandardCharsets.UTF_8));
            if (root.isJsonArray()) {
                JsonArray array = root.getAsJsonArray();
                for (JsonElement elem : array) {
                    if (elem.isJsonObject()) {
                        parseRecipe(elem.getAsJsonObject());
                    }
                }
            }
            IOUtils.closeQuietly(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void parseRecipe(JsonObject json) {
        String idStr = JsonUtils.getString(json, "id");
        ResourceLocation id = new ResourceLocation(idStr);
        JsonArray ingArray = JsonUtils.getJsonArray(json, "ingredients");
        List<HammerForgeRecipe.IngredientCount> ingredients = new ArrayList<>();
        for (JsonElement ingElem : ingArray) {
            JsonObject ingObj = ingElem.getAsJsonObject();
            Ingredient ingredient;
            if (ingObj.has("type") && ingObj.get("type").getAsString().equals("forge:ore_dict")) {
                ingredient = new net.minecraftforge.oredict.OreIngredient(JsonUtils.getString(ingObj, "ore"));
            } else {
                String itemName = JsonUtils.getString(ingObj, "item");
                net.minecraft.item.Item item = net.minecraft.item.Item.REGISTRY.getObject(new ResourceLocation(itemName));
                if (item == null || item == net.minecraft.init.Items.AIR) {
                    System.err.println("[HammersUnbound] Unknown item: " + itemName + " in recipe " + idStr);
                    continue;
                }
                ingredient = Ingredient.fromItem(item);
            }
            int count = JsonUtils.getInt(ingObj, "count", 1);
            ingredients.add(new HammerForgeRecipe.IngredientCount(ingredient, count));
        }
        JsonObject resultObj = JsonUtils.getJsonObject(json, "result");
        String resultItemName = JsonUtils.getString(resultObj, "item");
        net.minecraft.item.Item resultItem = net.minecraft.item.Item.REGISTRY.getObject(new ResourceLocation(resultItemName));
        if (resultItem == null || resultItem == net.minecraft.init.Items.AIR) {
            System.err.println("[HammersUnbound] Unknown result item: " + resultItemName + " in recipe " + idStr);
            return;
        }
        ItemStack result = new ItemStack(resultItem, JsonUtils.getInt(resultObj, "count", 1));
        RECIPES.add(new HammerForgeRecipe(id, ingredients, result));
    }
    public static List<HammerForgeRecipe> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }
    public static HammerForgeRecipe getRecipe(ResourceLocation id) {
        for (HammerForgeRecipe recipe : RECIPES) {
            if (recipe.getId().equals(id)) {
                return recipe;
            }
        }
        return null;
    }
}