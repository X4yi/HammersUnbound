package com.x4yi.hammersunbound.compat.jei;
import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.hammersunbound.init.ModBlocks;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import java.util.List;
public class HammerForgeRecipeCategory implements IRecipeCategory<HammerForgeRecipeWrapper> {
    private final IDrawable background;
    private final IDrawable icon;
    public HammerForgeRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 100);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.HAMMER_FORGE));
    }
    @Override
    public String getUid() {
        return HammerJEIPlugin.FORGE_CATEGORY;
    }
    @Override
    public String getTitle() {
        return I18n.format("tile.hammersunbound.hammer_forge.name");
    }
    @Override
    public String getModName() {
        return HammersUnbound.MODID;
    }
    @Override
    public IDrawable getBackground() {
        return background;
    }
    @Override
    public IDrawable getIcon() {
        return icon;
    }
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, HammerForgeRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, false, 130, 40);
        guiItemStacks.set(0, ingredients.getOutputs(ItemStack.class).get(0));
        int y = 10;
        int slot = 1;
        for (List<ItemStack> input : ingredients.getInputs(ItemStack.class)) {
            guiItemStacks.init(slot, true, 10, y);
            guiItemStacks.set(slot, input);
            y += 20;
            slot++;
        }
    }
}