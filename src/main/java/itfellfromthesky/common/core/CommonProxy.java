package itfellfromthesky.common.core;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import ichun.common.core.network.ChannelHandler;
import itfellfromthesky.common.ItFellFromTheSky;
import itfellfromthesky.common.block.BlockCompactPorkchop;
import itfellfromthesky.common.block.ItemBlockCompactPorkchop;
import itfellfromthesky.common.creativetab.CreativeTabItFellFromTheSky;
import itfellfromthesky.common.entity.EntityBlock;
import itfellfromthesky.common.entity.EntityMeteorite;
import itfellfromthesky.common.entity.EntityPigzilla;
import itfellfromthesky.common.entity.EntityTransformer;
import itfellfromthesky.common.network.PacketKillMeteorite;
import itfellfromthesky.common.network.PacketMeteorSpawn;
import itfellfromthesky.common.network.PacketMeteoriteInfo;
import itfellfromthesky.common.network.PacketRidePig;
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

        ItFellFromTheSky.channels = ChannelHandler.getChannelHandlers("ItFellFromTheSky", PacketMeteorSpawn.class, PacketKillMeteorite.class, PacketMeteoriteInfo.class, PacketRidePig.class);
    }
}
