package itfellfromthesky.client.render;

import itfellfromthesky.client.model.ModelMeteorite;
import itfellfromthesky.client.model.ModelPigBase;
import itfellfromthesky.common.entity.EntityMeteorite;
import itfellfromthesky.common.entity.EntityTransformer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderTransformer extends Render
{
    private static final ResourceLocation pigTextures = new ResourceLocation("textures/entity/pig/pig.png");

    public ModelPigBase modelBase;
    public ModelMeteorite modelMeteorite;

    //TODO set shadow sized based off bounding box of the block...?
    public RenderTransformer()
    {
        this.shadowSize = 10F;
        modelBase = new ModelPigBase();
        modelMeteorite = new ModelMeteorite();
    }

    public void doRender(EntityTransformer transformer, double posX, double posY, double posZ, float par8, float renderTick)
    {
        if(transformer.ticksExisted <= 2)
        {
            return;
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, (float)posZ);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.bindEntityTexture(transformer);

//        GL11.glScalef(-1.0F, -1.0F, 1.0F);

        GL11.glDisable(GL11.GL_LIGHTING);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);

        modelBase.render(transformer, renderTick, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        bindTexture(RenderMeteorite.pigBlock);

        GL11.glPushMatrix();
        GL11.glScalef(-40.01F, -40.01F, 40.01F);

        GL11.glRotatef(90F, 0F, 1F, 0F);

        GL11.glRotatef(-RenderTransformer.interpolateRotation(transformer.prevRotYaw, transformer.rotYaw, renderTick), 0F, 1F, 0F);

        GL11.glRotatef(RenderTransformer.interpolateRotation(transformer.prevRotPitch, transformer.rotPitch, renderTick), 0F, 0F, 1F);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)Math.pow(1.0F - MathHelper.clamp_float(((float)transformer.ticksExisted - 3 + renderTick) / 110F, 0.0F, 1.0F), 0.5D));

        modelMeteorite.render(transformer, 0F, 0F, 0F, 0F, 0F, 0.0625F);

        GL11.glPopMatrix();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glEnable(GL11.GL_LIGHTING);
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_BLEND);
    }


    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityTransformer)par1Entity, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
        return pigTextures;
    }

    public static float interpolateRotation(float par1, float par2, float par3)
    {
        float f3;

        for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return par1 + par3 * f3;
    }
}
