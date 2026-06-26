package com.x4yi.hammersunbound.init;
import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.hammersunbound.config.HammerMaterialData;
import com.x4yi.hammersunbound.config.SpikeHammerConfig;
import com.x4yi.hammersunbound.config.WarHammerConfig;
import com.x4yi.hammersunbound.item.base.HammerMaterialType;
import com.x4yi.hammersunbound.item.spikehammer.SpikeHammerItem;
import com.x4yi.hammersunbound.item.warhammer.WarHammerItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import java.util.EnumMap;
import java.util.Map;
@Mod.EventBusSubscriber(modid = HammersUnbound.MODID)
public class ModItems {

    public static final Map<HammerMaterialType, WarHammerItem> WARHAMMERS = new EnumMap<>(HammerMaterialType.class);
    public static final Map<HammerMaterialType, SpikeHammerItem> SPIKEHAMMERS = new EnumMap<>(HammerMaterialType.class);

    public static WarHammerItem warhammer_wood;
    public static WarHammerItem warhammer_stone;
    public static WarHammerItem warhammer_iron;
    public static WarHammerItem warhammer_gold;
    public static WarHammerItem warhammer_diamond;
    public static SpikeHammerItem spikehammer_wood;
    public static SpikeHammerItem spikehammer_stone;
    public static SpikeHammerItem spikehammer_iron;
    public static SpikeHammerItem spikehammer_gold;
    public static SpikeHammerItem spikehammer_diamond;
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        warhammer_wood = new WarHammerItem("wood", WarHammerConfig.getAllMaterials().get("wood").data);
        warhammer_wood.setRegistryName("warhammer_wood");
        warhammer_wood.setUnlocalizedName(HammersUnbound.MODID + ".warhammer_wood");
        registry.register(warhammer_wood);
        WARHAMMERS.put(HammerMaterialType.WOOD, warhammer_wood);
        warhammer_stone = new WarHammerItem("stone", WarHammerConfig.getAllMaterials().get("stone").data);
        warhammer_stone.setRegistryName("warhammer_stone");
        warhammer_stone.setUnlocalizedName(HammersUnbound.MODID + ".warhammer_stone");
        registry.register(warhammer_stone);
        WARHAMMERS.put(HammerMaterialType.STONE, warhammer_stone);
        warhammer_iron = new WarHammerItem("iron", WarHammerConfig.getAllMaterials().get("iron").data);
        warhammer_iron.setRegistryName("warhammer_iron");
        warhammer_iron.setUnlocalizedName(HammersUnbound.MODID + ".warhammer_iron");
        registry.register(warhammer_iron);
        WARHAMMERS.put(HammerMaterialType.IRON, warhammer_iron);
        warhammer_gold = new WarHammerItem("gold", WarHammerConfig.getAllMaterials().get("gold").data);
        warhammer_gold.setRegistryName("warhammer_gold");
        warhammer_gold.setUnlocalizedName(HammersUnbound.MODID + ".warhammer_gold");
        registry.register(warhammer_gold);
        WARHAMMERS.put(HammerMaterialType.GOLD, warhammer_gold);
        warhammer_diamond = new WarHammerItem("diamond", WarHammerConfig.getAllMaterials().get("diamond").data);
        warhammer_diamond.setRegistryName("warhammer_diamond");
        warhammer_diamond.setUnlocalizedName(HammersUnbound.MODID + ".warhammer_diamond");
        registry.register(warhammer_diamond);
        WARHAMMERS.put(HammerMaterialType.DIAMOND, warhammer_diamond);

        spikehammer_wood = new SpikeHammerItem("wood", SpikeHammerConfig.getAllMaterials().get("wood").data);
        spikehammer_wood.setRegistryName("spikehammer_wood");
        spikehammer_wood.setUnlocalizedName(HammersUnbound.MODID + ".spikehammer_wood");
        registry.register(spikehammer_wood);
        SPIKEHAMMERS.put(HammerMaterialType.WOOD, spikehammer_wood);
        spikehammer_stone = new SpikeHammerItem("stone", SpikeHammerConfig.getAllMaterials().get("stone").data);
        spikehammer_stone.setRegistryName("spikehammer_stone");
        spikehammer_stone.setUnlocalizedName(HammersUnbound.MODID + ".spikehammer_stone");
        registry.register(spikehammer_stone);
        SPIKEHAMMERS.put(HammerMaterialType.STONE, spikehammer_stone);
        spikehammer_iron = new SpikeHammerItem("iron", SpikeHammerConfig.getAllMaterials().get("iron").data);
        spikehammer_iron.setRegistryName("spikehammer_iron");
        spikehammer_iron.setUnlocalizedName(HammersUnbound.MODID + ".spikehammer_iron");
        registry.register(spikehammer_iron);
        SPIKEHAMMERS.put(HammerMaterialType.IRON, spikehammer_iron);
        spikehammer_gold = new SpikeHammerItem("gold", SpikeHammerConfig.getAllMaterials().get("gold").data);
        spikehammer_gold.setRegistryName("spikehammer_gold");
        spikehammer_gold.setUnlocalizedName(HammersUnbound.MODID + ".spikehammer_gold");
        registry.register(spikehammer_gold);
        SPIKEHAMMERS.put(HammerMaterialType.GOLD, spikehammer_gold);
        spikehammer_diamond = new SpikeHammerItem("diamond", SpikeHammerConfig.getAllMaterials().get("diamond").data);
        spikehammer_diamond.setRegistryName("spikehammer_diamond");
        spikehammer_diamond.setUnlocalizedName(HammersUnbound.MODID + ".spikehammer_diamond");
        registry.register(spikehammer_diamond);
        SPIKEHAMMERS.put(HammerMaterialType.DIAMOND, spikehammer_diamond);
    }
}