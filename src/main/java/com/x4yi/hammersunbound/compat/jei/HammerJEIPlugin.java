package com.x4yi.hammersunbound.compat.jei;
import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.hammersunbound.crafting.HammerForgeRecipe;
import com.x4yi.hammersunbound.crafting.HammerRecipeManager;
import com.x4yi.hammersunbound.init.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
@JEIPlugin
public class HammerJEIPlugin implements IModPlugin {
    public static final String FORGE_CATEGORY = HammersUnbound.MODID + ".hammer_forge";
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new HammerForgeRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }
    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(HammerForgeRecipe.class, HammerForgeRecipeWrapper::new, FORGE_CATEGORY);
        List<HammerForgeRecipe> recipes = HammerRecipeManager.getRecipes();
        registry.addRecipes(recipes, FORGE_CATEGORY);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.HAMMER_FORGE), FORGE_CATEGORY);
    }
}