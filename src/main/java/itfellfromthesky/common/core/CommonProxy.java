package itfellfromthesky.common.core;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import itfellfromthesky.common.ItFellFromTheSky;
import itfellfromthesky.common.block.BlockCompactPorkchop;
import itfellfromthesky.common.block.ItemBlockCompactPorkchop;
import itfellfromthesky.common.creativetab.CreativeTabItFellFromTheSky;
import itfellfromthesky.common.entity.*;
import itfellfromthesky.common.network.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CommonProxy
{
    public void initMod()
    {
        EntityRegistry.registerModEntity(EntityBlock.class, "itfellfromthesky_block", 140, ItFellFromTheSky.instance, 160, 20, true);
        EntityRegistry.registerModEntity(EntityMeteorite.class, "itfellfromthesky_meteorite", 141, ItFellFromTheSky.instance, 160, Integer.MAX_VALUE, true);
        EntityRegistry.registerModEntity(EntityTransformer.class, "itfellfromthesky_transformer", 142, ItFellFromTheSky.instance, 160, 20, true);
        EntityRegistry.registerModEntity(EntityPigzilla.class, "itfellfromthesky_pigzilla", 143, ItFellFromTheSky.instance, 160, 20, true);
//        EntityRegistry.registerModEntity(EntityPigPart.class, "itfellfromthesky_pigpart", 144, ItFellFromTheSky.instance, 160, 20, true);

        ItFellFromTheSky.creativeTabPorkchop = new CreativeTabItFellFromTheSky();

        ItFellFromTheSky.blockCompactPorkchop = (new BlockCompactPorkchop()).setCreativeTab(ItFellFromTheSky.creativeTabPorkchop).setHardness(0.8F).setBlockName("compactPorkchop");
        GameRegistry.registerBlock(ItFellFromTheSky.blockCompactPorkchop, ItemBlockCompactPorkchop.class, "compactPorkchop");

        GameRegistry.addShapelessRecipe(new ItemStack(ItFellFromTheSky.blockCompactPorkchop, 1), Items.porkchop, Items.porkchop, Items.porkchop, Items.porkchop, Items.porkchop, Items.porkchop, Items.porkchop, Items.porkchop, Items.porkchop);
        GameRegistry.addShapelessRecipe(new ItemStack(Items.porkchop, 9), ItFellFromTheSky.blockCompactPorkchop);

        ItFellFromTheSky.channels = NetworkRegistry.INSTANCE.newChannel("ItFellFromTheSky", new ChannelHandler(PacketMeteorSpawn.class, PacketKillMeteorite.class, PacketMeteoriteInfo.class, PacketRidePig.class));
    }
}
