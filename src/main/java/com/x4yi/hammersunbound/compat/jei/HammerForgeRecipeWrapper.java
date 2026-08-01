package com.x4yi.hammersunbound.compat.jei;
import com.x4yi.hammersunbound.crafting.HammerForgeRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
public class HammerForgeRecipeWrapper implements IRecipeWrapper {
    private final HammerForgeRecipe recipe;
    public HammerForgeRecipeWrapper(HammerForgeRecipe recipe) {
        this.recipe = recipe;
    }
    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputs = new ArrayList<>();
        for (HammerForgeRecipe.IngredientCount ic : recipe.getIngredients()) {
            ItemStack[] matching = ic.getIngredient().getMatchingStacks();
            List<ItemStack> list = new ArrayList<>();
            for (ItemStack stack : matching) {
                ItemStack copy = stack.copy();
                copy.setCount(ic.getCount());
                list.add(copy);
            }
            inputs.add(list);
        }
        ingredients.setInputLists(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, recipe.getResult());
    }
    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int y = 14;
        for (HammerForgeRecipe.IngredientCount ic : recipe.getIngredients()) {
            minecraft.fontRenderer.drawString("x" + ic.getCount(), 30, y, 0xFFFFFF);
            y += 20;
        }
    }
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}