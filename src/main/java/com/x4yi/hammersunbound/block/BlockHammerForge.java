package com.x4yi.hammersunbound.block;
import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.hammersunbound.init.ModCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
public class BlockHammerForge extends Block {
    public static final String NAME = "hammer_forge";
    public BlockHammerForge() {
        super(Material.IRON);
        this.setRegistryName(HammersUnbound.MODID, NAME);
        this.setUnlocalizedName(HammersUnbound.MODID + "." + NAME);
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        this.setHarvestLevel("pickaxe", 1);
        this.setCreativeTab(ModCreativeTabs.HAMMERS_UNBOUND);
    }
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(HammersUnbound.instance, com.x4yi.hammersunbound.network.ModGuiHandler.HAMMER_FORGE_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}