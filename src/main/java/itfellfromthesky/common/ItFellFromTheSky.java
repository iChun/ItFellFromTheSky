package itfellfromthesky.common;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.relauncher.Side;
import ichun.common.core.config.Config;
import ichun.common.core.config.ConfigHandler;
import ichun.common.core.config.IConfigUser;
import ichun.common.core.updateChecker.ModVersionChecker;
import ichun.common.core.updateChecker.ModVersionInfo;
import ichun.common.iChunUtil;
import itfellfromthesky.common.core.ChunkLoadHandler;
import itfellfromthesky.common.core.CommonProxy;
import itfellfromthesky.common.core.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;

@Mod(modid = "ItFellFromTheSky", name = "ItFellFromTheSky",
        version = ItFellFromTheSky.version,
        dependencies = "required-after:iChunUtil@[" + iChunUtil.versionMC +".0.0,)",
        acceptableRemoteVersions = "[" + iChunUtil.versionMC +".0.0," + iChunUtil.versionMC + ".1.0)"
)
public class ItFellFromTheSky
        implements IConfigUser
{
    public static final String version = iChunUtil.versionMC +".0.0";

    @Mod.Instance("ItFellFromTheSky")
    public static ItFellFromTheSky instance;

    @SidedProxy(clientSide = "itfellfromthesky.client.core.ClientProxy", serverSide = "itfellfromthesky.common.core.CommonProxy")
    public static CommonProxy proxy;

    private static final Logger logger = LogManager.getLogger("ItFellFromTheSky");

    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    public static Config config;

    public static Block blockCompactPorkchop;

    public static CreativeTabs creativeTabPorkchop;

    public static boolean hasHatsMod;

    @Override
    public boolean onConfigChange(Config cfg, Property prop)
    {
        return true;
    }

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.createConfig(event.getSuggestedConfigurationFile(), "itfellfromthesky", "It Fell From The Sky", logger, instance);

        config.setCurrentCategory("gameplay", "itfellfromthesky.config.cat.gameplay.name", "itfellfromthesky.config.cat.gameplay.comment");
        config.createIntBoolProperty("summonPigzillaNeedsOp", "itfellfromthesky.config.prop.summonPigzillaNeedsOp.name", "itfellfromthesky.config.prop.summonPigzillaNeedsOp.comment", true, false, false);

        proxy.initMod();

        EventHandler handler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadHandler());

        ModVersionChecker.register_iChunMod(new ModVersionInfo("ItFellFromTheSky", iChunUtil.versionOfMC, version, false));
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
    }

    @Mod.EventHandler
    public void postLoad(FMLPostInitializationEvent event)
    {
        hasHatsMod = Loader.isModLoaded("Hats");
    }

    @Mod.EventHandler
    public  void onServerStopped(FMLServerStoppedEvent event)
    {
        ChunkLoadHandler.tickets.clear();
    }

    public static void console(String s, boolean warning)
    {
        StringBuilder sb = new StringBuilder();
        logger.log(warning ? Level.WARN : Level.INFO, sb.append("[").append(version).append("-ModJam] ").append(s).toString());
    }
}
