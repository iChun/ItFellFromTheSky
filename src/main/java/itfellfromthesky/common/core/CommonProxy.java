package itfellfromthesky.common.core;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import itfellfromthesky.common.ItFellFromTheSky;
import itfellfromthesky.common.entity.*;
import itfellfromthesky.common.network.*;

public class CommonProxy
{
    public void initMod()
    {
        EntityRegistry.registerModEntity(EntityBlock.class, "itfellfromthesky_block", 140, ItFellFromTheSky.instance, 160, 20, true);
        EntityRegistry.registerModEntity(EntityMeteorite.class, "itfellfromthesky_meteorite", 141, ItFellFromTheSky.instance, 160, Integer.MAX_VALUE, true);
        EntityRegistry.registerModEntity(EntityTransformer.class, "itfellfromthesky_transformer", 142, ItFellFromTheSky.instance, 160, 20, true);
        EntityRegistry.registerModEntity(EntityPigzilla.class, "itfellfromthesky_pigzilla", 143, ItFellFromTheSky.instance, 160, 20, true);
//        EntityRegistry.registerModEntity(EntityPigPart.class, "itfellfromthesky_pigpart", 144, ItFellFromTheSky.instance, 160, 20, true);

        ItFellFromTheSky.channels = NetworkRegistry.INSTANCE.newChannel("ItFellFromTheSky", new ChannelHandler(PacketMeteorSpawn.class, PacketKillMeteorite.class, PacketMeteoriteInfo.class, PacketRidePig.class));
    }
}
