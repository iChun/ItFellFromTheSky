package itfellfromthesky.common;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.relauncher.Side;
import itfellfromthesky.common.core.ChunkLoadHandler;
import itfellfromthesky.common.core.CommonProxy;
import itfellfromthesky.common.core.EventHandler;
import itfellfromthesky.common.core.ObfHelper;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;

@Mod(modid = "ItFellFromTheSky", name = "ItFellFromTheSky",
        version = ItFellFromTheSky.version,
        dependencies = "required-after:Forge@[10.12.1.1081,)"
            )
public class ItFellFromTheSky
{
    public static final String version = "3.0.0";

    @Mod.Instance("ItFellFromTheSky")
    public static ItFellFromTheSky instance;

    @SidedProxy(clientSide = "itfellfromthesky.client.core.ClientProxy", serverSide = "itfellfromthesky.common.core.CommonProxy")
    public static CommonProxy proxy;

    private static final Logger logger = LogManager.getLogger("ItFellFromTheSky");

    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        ObfHelper.detectObfuscation();

        proxy.initMod();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadHandler());
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
    }

    @Mod.EventHandler
    public void postLoad(FMLPostInitializationEvent event)
    {

    }

    public static void console(String s, boolean warning)
    {
        StringBuilder sb = new StringBuilder();
        logger.log(warning ? Level.WARN : Level.INFO, sb.append("[").append(version).append("] ").append(s).toString());
    }
}
