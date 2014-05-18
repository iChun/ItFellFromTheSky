package itfellfromthesky.client.render;

import itfellfromthesky.common.entity.EntityPigPart;
import itfellfromthesky.common.entity.EntityPigzilla;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderPigzilla extends Render
{
    private static final ResourceLocation pigTextures = new ResourceLocation("textures/entity/pig/pig.png");

    public ModelPig modelBase;

    public RenderPigzilla()
    {
        this.shadowSize = 10F;
        modelBase = new ModelPig();
        modelBase.isChild = false;
    }

    public void doRender(EntityPigzilla pig, double posX, double posY, double posZ, float par8, float renderTick)
    {
        if(pig.ticksExisted <= 2)
        {
            return;
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, (float)posZ);
        this.bindEntityTexture(pig);

        GL11.glScalef(-40.0F, -40.0F, 40.0F);

        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glTranslatef(0.0F, -1.5F, 0.0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);

        GL11.glRotatef(interpolateRotation(pig.prevRenderYawOffset, pig.renderYawOffset, renderTick), 0F, 1F, 0F);
        modelBase.render(pig, pig.limbSwing, 0.4F, 2F, interpolateRotation(pig.prevRotationYaw, pig.rotationYaw, renderTick) - interpolateRotation(pig.prevRenderYawOffset, pig.renderYawOffset, renderTick), interpolateRotation(pig.prevRotationPitch, pig.rotationPitch, renderTick), 0.0625F);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_BLEND);
    }


    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        if(par1Entity instanceof EntityPigPart)
        {
            return;
        }
        this.doRender((EntityPigzilla)par1Entity, par2, par4, par6, par8, par9);
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
