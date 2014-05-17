package itfellfromthesky.client.render;

import itfellfromthesky.client.model.ModelMeteorite;
import itfellfromthesky.common.entity.EntityBlock;
import itfellfromthesky.common.entity.EntityMeteorite;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderMeteorite extends Render
{
    public ModelMeteorite model = new ModelMeteorite();

    public static final ResourceLocation pigBlock = new ResourceLocation("itfellfromthesky", "textures/model/meteorite.png");

    //TODO set shadow sized based off bounding box of the block...?
    public RenderMeteorite()
    {
        this.shadowSize = 10F;
    }

    public void doRender(EntityMeteorite entBlock, double posX, double posY, double posZ, float par8, float renderTick)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, (float)posZ);
        this.bindEntityTexture(entBlock);

        GL11.glRotatef(-90F, 0F, 1F, 0F);

        GL11.glRotatef(interpolateRotation(entBlock.prevRotYaw, entBlock.rotYaw, renderTick), 0F, 1F, 0F);
        GL11.glRotatef(interpolateRotation(entBlock.prevRotPitch, entBlock.rotPitch, renderTick), 0F, 0F, 1F);

        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glScalef(40F, 40F, 40F);

        model.render(entBlock, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

//        renderBlock.setRenderBoundsFromBlock(Blocks.iron_block);
//        renderBlock.renderBlockAsItem(Blocks.mob_spawner, 0, 1.0F);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_BLEND);
    }


    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityMeteorite)par1Entity, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
        return pigBlock;
    }

    private float interpolateRotation(float par1, float par2, float par3)
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
