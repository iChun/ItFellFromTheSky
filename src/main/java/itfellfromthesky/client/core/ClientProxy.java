package itfellfromthesky.client.core;

import cpw.mods.fml.client.registry.RenderingRegistry;
import itfellfromthesky.client.render.RenderBlock;
import itfellfromthesky.client.render.RenderMeteorite;
import itfellfromthesky.common.core.CommonProxy;
import itfellfromthesky.common.entity.EntityBlock;
import itfellfromthesky.common.entity.EntityMeteorite;

public class ClientProxy extends CommonProxy
{
    public void initMod()
    {
        super.initMod();
        RenderingRegistry.registerEntityRenderingHandler(EntityBlock.class, new RenderBlock());
        RenderingRegistry.registerEntityRenderingHandler(EntityMeteorite.class, new RenderMeteorite());
    }
}
