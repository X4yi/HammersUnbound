package com.x4yi.hammersunbound.crafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import java.util.List;
public class HammerForgeRecipe {
    private final ResourceLocation id;
    private final List<IngredientCount> ingredients;
    private final ItemStack result;
    public HammerForgeRecipe(ResourceLocation id, List<IngredientCount> ingredients, ItemStack result) {
        this.id = id;
        this.ingredients = ingredients;
        this.result = result;
    }
    public ResourceLocation getId() {
        return id;
    }
    public List<IngredientCount> getIngredients() {
        return ingredients;
    }
    public ItemStack getResult() {
        return result;
    }
    public static class IngredientCount {
        private final Ingredient ingredient;
        private final int count;
        public IngredientCount(Ingredient ingredient, int count) {
            this.ingredient = ingredient;
            this.count = count;
        }
        public Ingredient getIngredient() {
            return ingredient;
        }
        public int getCount() {
            return count;
        }
    }
}