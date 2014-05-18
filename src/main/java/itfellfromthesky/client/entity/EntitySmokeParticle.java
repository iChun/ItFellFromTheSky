package itfellfromthesky.client.entity;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EntitySmokeParticle extends EntityFX
{
    public float jitter;

    public static int maxAge = 600;

    public EntitySmokeParticle(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.setParticleTextureIndex(4 + rand.nextInt(3));
        this.setSize(0.01F, 0.01F);
        this.particleGravity = 0.00F;
        this.particleMaxAge = -5;
        this.particleScale = 150F;
        this.particleAlpha = (float)(Math.random() * 0.5D + 0.3D);

        jitter = 0.2F * rand.nextFloat() - 0.1F;
    }

    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.particleMaxAge++ > maxAge || particleScale < 6F)
        {
            this.setDead();
        }

        Material material = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial();

        if (material.isLiquid() || material.isSolid())
        {
            double d0 = (double)((float)(MathHelper.floor_double(this.posY) + 1) - BlockLiquid.getLiquidHeightPercent(this.worldObj.getBlockMetadata(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))));

            if (this.posY < d0)
            {
                this.setDead();
            }
        }
    }

    @Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        if(particleMaxAge < 0)
        {
            return;
        }

        float f6 = (float)this.particleTextureIndexX / 16.0F;
        float f7 = f6 + 0.0624375F;
        float f8 = (float)this.particleTextureIndexY / 16.0F;
        float f9 = f8 + 0.0624375F;
        float f10 = 0.1F * this.particleScale;

        if (this.particleIcon != null)
        {
            f6 = this.particleIcon.getMinU();
            f7 = this.particleIcon.getMaxU();
            f8 = this.particleIcon.getMinV();
            f9 = this.particleIcon.getMaxV();
        }


        //yellow = 255, 228, 0
        //red = 255, 0, 0
        //gray = 140, 140, 140

        if(particleMaxAge < 100F * (1.0F - jitter))
        {
            float prog = MathHelper.clamp_float(((float)Math.pow((particleMaxAge + par2) / (100F * (1.0F - jitter)), 0.5D)), 0.0F, 1.0F);
            particleRed = 1.0F;
            particleGreen = 1.0F - ((255F - 253F) / 255F) * prog + jitter;
            particleBlue = 1.0F - ((255F - 87F) / 255F) * prog + jitter;

            particleScale = 150F - 30F * (1.0F - jitter) * prog;
        }
        else if(particleMaxAge < 200F * (1.0F - jitter)) // 77
        {
            float prog = MathHelper.clamp_float(((float)Math.pow((particleMaxAge - 100F * (1.0F - jitter) + par2) / (100F * (1.0F - jitter)), 0.5D)), 0.0F, 1.0F);
            particleRed = 1.0F;
            particleGreen = (253F / 255F) - ((253F - 77F) / 255F) * (prog) + jitter;
            particleBlue = (87F / 255F) - ((87F - 77F) / 255F) * (prog) + jitter;
            particleScale = 150F - 30F * (1.0F - jitter) - 35F * (1.0F - jitter) * prog;
        }
        else
        {
            float prog = MathHelper.clamp_float(((float)Math.pow((particleMaxAge - 200F * (1.0F - jitter) + par2) / (60F * (1.0F - jitter)), 0.5D)), 0.0F, 1.0F);
            float prog2 = MathHelper.clamp_float(((float)Math.pow((particleMaxAge - 200F * (1.0F - jitter) + par2) / (60F * (1.0F - jitter)), 0.5D)), 0.0F, 1.5F);
            particleRed = 1.0F - ((255F - 140F) / 255F) * prog + jitter;
            particleGreen = 0.0F + ((255F - 140F) / 255F) * prog + jitter;
            particleBlue = 0.0F + ((255F - 140F) / 255F) * prog + jitter;
            particleScale = 150F - 30F * (1.0F - jitter) - 35F * (1.0F - jitter) - 25F * (1.0F - jitter) * prog2;

            if(maxAge > 500)
            {
                particleScale *= 0.9F;
            }
        }

        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
        par1Tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        par1Tessellator.addVertexWithUV((double)(f11 - par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 - par5 * f10 - par7 * f10), (double)f7, (double)f9);
        par1Tessellator.addVertexWithUV((double)(f11 - par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 - par5 * f10 + par7 * f10), (double)f7, (double)f8);
        par1Tessellator.addVertexWithUV((double)(f11 + par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 + par5 * f10 + par7 * f10), (double)f6, (double)f8);
        par1Tessellator.addVertexWithUV((double)(f11 + par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 + par5 * f10 - par7 * f10), (double)f6, (double)f9);
    }

}
