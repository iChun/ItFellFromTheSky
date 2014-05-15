package itfellfromthesky.common;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import itfellfromthesky.common.core.CommonProxy;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        proxy.initMod();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
    }

    @Mod.EventHandler
    public void postLoad(FMLPostInitializationEvent event)
    {

    }

    public void console(String s, boolean warning)
    {
        StringBuilder sb = new StringBuilder();
        logger.log(warning ? Level.WARN : Level.INFO, sb.append("[").append(version).append("] ").append(s).toString());
    }
}
