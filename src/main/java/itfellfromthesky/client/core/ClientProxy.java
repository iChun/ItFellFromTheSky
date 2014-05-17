package itfellfromthesky.client.core;

import cpw.mods.fml.client.registry.RenderingRegistry;
import itfellfromthesky.client.render.RenderBlock;
import itfellfromthesky.client.render.RenderMeteorite;
import itfellfromthesky.client.render.RenderPigzilla;
import itfellfromthesky.client.render.RenderTransformer;
import itfellfromthesky.common.core.CommonProxy;
import itfellfromthesky.common.entity.*;

public class ClientProxy extends CommonProxy
{
    public void initMod()
    {
        super.initMod();
        RenderingRegistry.registerEntityRenderingHandler(EntityBlock.class, new RenderBlock());
        RenderingRegistry.registerEntityRenderingHandler(EntityMeteorite.class, new RenderMeteorite());
        RenderingRegistry.registerEntityRenderingHandler(EntityTransformer.class, new RenderTransformer());
        RenderingRegistry.registerEntityRenderingHandler(EntityPigzilla.class, new RenderPigzilla());
        RenderingRegistry.registerEntityRenderingHandler(EntityPigPart.class, new RenderPigzilla());
    }
}
