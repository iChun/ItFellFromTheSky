package itfellfromthesky.client.render;

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
    public RenderBlocks renderBlock = new RenderBlocks();

    //TODO set shadow sized based off bounding box of the block...?
    public RenderMeteorite()
    {
        this.shadowSize = 10F;
    }

    public void doRender(EntityMeteorite entBlock, double posX, double posY, double posZ, float par8, float renderTick)
    {
        int i = MathHelper.floor_double(entBlock.posX);
        int j = MathHelper.floor_double(entBlock.posY);
        int k = MathHelper.floor_double(entBlock.posZ);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, (float)posZ);
        this.bindEntityTexture(entBlock);

        GL11.glRotatef(-90F, 0F, 1F, 0F);

        GL11.glRotatef(interpolateRotation(entBlock.prevRotYaw, entBlock.rotYaw, renderTick), 0F, 1F, 0F);
        GL11.glRotatef(interpolateRotation(entBlock.prevRotPitch, entBlock.rotPitch, renderTick), 0F, 0F, 1F);

        GL11.glDisable(GL11.GL_LIGHTING);

//        int ii = entBlock.getBrightnessForRender(renderTick);
//        int jj = ii % 0x10000;
//        int kk = ii / 0x10000;
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)jj / 1.0F, (float)kk / 1.0F);

        renderBlock.blockAccess = entBlock.worldObj;
        renderBlock.useInventoryTint = true;

        GL11.glScalef(20F, 20F, 20F);

        renderBlock.setRenderBoundsFromBlock(Blocks.iron_block);
        renderBlock.renderBlockAsItem(Blocks.iron_block, 0, 1.0F);

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
        return TextureMap.locationBlocksTexture;
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
