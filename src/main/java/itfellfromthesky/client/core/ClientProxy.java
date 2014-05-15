package itfellfromthesky.client.core;

import cpw.mods.fml.client.registry.RenderingRegistry;
import itfellfromthesky.client.render.RenderBlock;
import itfellfromthesky.common.core.CommonProxy;
import itfellfromthesky.common.entity.EntityBlock;

public class ClientProxy extends CommonProxy
{
    public void initMod()
    {
        super.initMod();
        RenderingRegistry.registerEntityRenderingHandler(EntityBlock.class, new RenderBlock());
    }
}
