package itfellfromthesky.common.core;

import cpw.mods.fml.common.registry.EntityRegistry;
import itfellfromthesky.common.ItFellFromTheSky;
import itfellfromthesky.common.entity.EntityBlock;
import itfellfromthesky.common.entity.EntityMeteorite;

public class CommonProxy
{
    public void initMod()
    {
        EntityRegistry.registerModEntity(EntityBlock.class, "itfellfromthesky_block", 140, ItFellFromTheSky.instance, 160, 20, true);
        EntityRegistry.registerModEntity(EntityMeteorite.class, "itfellfromthesky_meteorite", 141, ItFellFromTheSky.instance, 160, 20, true);
    }
}
