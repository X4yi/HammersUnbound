package com.x4yi.hammersunbound.network;
import com.x4yi.hammersunbound.crafting.HammerForgeRecipe;
import com.x4yi.hammersunbound.crafting.HammerRecipeManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
public class PacketForgeHammer implements IMessage {
    private ResourceLocation recipeId;
    public PacketForgeHammer() {
    }
    public PacketForgeHammer(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        String idStr = ByteBufUtils.readUTF8String(buf);
        this.recipeId = new ResourceLocation(idStr);
    }
    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.recipeId.toString());
    }
    public static class Handler implements IMessageHandler<PacketForgeHammer, IMessage> {
        @Override
        public IMessage onMessage(PacketForgeHammer message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                HammerForgeRecipe recipe = HammerRecipeManager.getRecipe(message.recipeId);
                if (recipe != null) {
                    boolean hasAll = true;
                    for (HammerForgeRecipe.IngredientCount ic : recipe.getIngredients()) {
                        int found = 0;
                        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                            ItemStack stack = player.inventory.getStackInSlot(i);
                            if (ic.getIngredient().apply(stack)) {
                                found += stack.getCount();
                            }
                        }
                        if (found < ic.getCount()) {
                            hasAll = false;
                            break;
                        }
                    }
                    if (hasAll) {
                        for (HammerForgeRecipe.IngredientCount ic : recipe.getIngredients()) {
                            int toRemove = ic.getCount();
                            for (int i = 0; i < player.inventory.getSizeInventory() && toRemove > 0; i++) {
                                ItemStack stack = player.inventory.getStackInSlot(i);
                                if (ic.getIngredient().apply(stack)) {
                                    int count = Math.min(stack.getCount(), toRemove);
                                    player.inventory.decrStackSize(i, count);
                                    toRemove -= count;
                                }
                            }
                        }
                        ItemStack result = recipe.getResult().copy();
                        if (!player.inventory.addItemStackToInventory(result)) {
                            player.dropItem(result, false);
                        }
                        player.world.playSound(null, player.getPosition(), net.minecraft.init.SoundEvents.BLOCK_ANVIL_USE, net.minecraft.util.SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }
            });
            return null;
        }
    }
}