package itfellfromthesky.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ibxm.Channel;
import itfellfromthesky.common.core.ChunkLoadHandler;
import itfellfromthesky.common.network.ChannelHandler;
import itfellfromthesky.common.network.PacketRidePig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.List;

public class EntityPigzilla extends Entity
{
    public EntityPigPart[] parts;
    public EntityPigPart partHead;
    public EntityPigPart partBody;
    public EntityPigPart partLeg1;
    public EntityPigPart partLeg2;
    public EntityPigPart partLeg3;
    public EntityPigPart partLeg4;

    public float prevRenderYawOffset;
    public float renderYawOffset;

    public float prevLimbSwingAmount;
    public float limbSwingAmount;

    public float limbSwing;

    public int idleTimeout;

    public EntityLivingBase watchedEntity;

    public EntityPigzilla(World world)
    {
        super(world);
        parts = new EntityPigPart[] { partHead = new EntityPigPart(this, "head", 8F / 16F * 40F, 8F / 16F * 40F), partBody = new EntityPigPart(this, "body", 12F / 16F * 40F, 8F / 16F * 40F), partLeg1 = new EntityPigPart(this, "leg1", 4F / 16F * 40F, 6F / 16F * 40F), partLeg2 = new EntityPigPart(this, "leg2", 4F / 16F * 40F, 6F / 16F * 40F), partLeg3 = new EntityPigPart(this, "leg3", 4F / 16F * 40F, 6F / 16F * 40F), partLeg4 = new EntityPigPart(this, "leg4", 4F / 16F * 40F, 6F / 16F * 40F)};
        setSize(25F, 10F);

        renderDistanceWeight = 60D;
        ignoreFrustumCheck = true;

        stepHeight = 2.2F;
    }

    public EntityPigzilla(World world, EntityTransformer trans)
    {
        this(world);

        setTargetedRenderYawOffset(360F * rand.nextFloat());

        setLocationAndAngles(trans.posX, trans.boundingBox.minY - trans.height / 2F, trans.posZ, 0.0F, 0.0F);

        prevRotationYaw = rotationYaw = prevRenderYawOffset = renderYawOffset = trans.getOriginRot() - 180F;

        dataWatcher.updateObject(22, renderYawOffset);

        double pX = (double)(-MathHelper.sin(renderYawOffset / 180.0F * (float)Math.PI));
        double pZ = (double)(MathHelper.cos(renderYawOffset / 180.0F * (float)Math.PI));

        float f2 = MathHelper.sqrt_double(pX * pX + pZ * pZ);
        pX /= (double)f2;
        pZ /= (double)f2;
        pX *= 10D / 16D * 40D;
        pZ *= 10D / 16D * 40D;

        setPosition(posX - pX, posY, posZ - pZ);
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(20, 0F); //targeted render yaw
        dataWatcher.addObject(21, (byte)0); //is idle?
        dataWatcher.addObject(22, 0F); // spawn render offset
        dataWatcher.addObject(23, -1); //watched entity ID
    }

    public void setTargetedRenderYawOffset(float f)
    {
        dataWatcher.updateObject(20, f);
    }

    public float getTargetedRenderYawOffset()
    {
        return dataWatcher.getWatchableObjectFloat(20);
    }

    public void setIdle(boolean flag)
    {
        if(flag)
        {
            dataWatcher.updateObject(21, (byte)1);
        }
        else
        {
            dataWatcher.updateObject(21, (byte)0);
        }
    }

    public boolean getIdle()
    {
        return (this.dataWatcher.getWatchableObjectByte(21) & 1) != 0;
    }

    public void setWatchedEntity(EntityLivingBase ent)
    {
        if(ent != null)
        {
            watchedEntity = ent;
            dataWatcher.updateObject(23, ent.getEntityId());
        }
        else
        {
            watchedEntity = null;
            dataWatcher.updateObject(23, -1);
        }
    }

    public int getWatchedEntityId()
    {
        return dataWatcher.getWatchableObjectInt(23);
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public Entity[] getParts()
    {
        return parts;
    }

    @Override
    protected void dealFireDamage(int par1)
    {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderOnFire()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public float getShadowSize()
    {
        return 25F;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        prevLimbSwingAmount = limbSwingAmount;
        prevRenderYawOffset = renderYawOffset;

        if(ticksExisted < 10)
        {
            if(worldObj.isRemote && ticksExisted == 1)
            {
                prevRotationYaw = rotationYaw =prevRenderYawOffset = renderYawOffset = dataWatcher.getWatchableObjectFloat(22);
                lastTickPosY -= height / 2F;
                prevPosY -= height / 2F;
                posY -= height / 2F;
            }
            return;
        }
//        faceEntity(Minecraft.getMinecraft().thePlayer, 0.6F, 0.6F);

        motionY -= 0.02D;

        moveEntity(motionX, motionY, motionZ);

        double velo = Math.sqrt(motionX * motionX + motionZ * motionZ);

        limbSwing += velo / 0.1D * 0.025D;

        if(velo <= 0.025D)
        {
            motionX = motionZ = 0.0D;
            limbSwing = 2.375F;
        }
        double yawRad = Math.toRadians(renderYawOffset);

        double sin = Math.sin(yawRad);
        double cos = Math.cos(yawRad);

//        partHead.moveTo(posX - (10F / 16F * 40F) * sin, posY + (8F / 16F * 40F) + (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI)) * 16D, posZ + (10F / 16F * 40F) * cos);
//
//        partBody.moveTo(posX, posY + (6F / 16F * 40F), posZ);
//
//        partLeg1.moveTo(posX - (5F / 16F * 40F) * sin + (3F / 16F * 40F) * cos, posY, posZ + (5F / 16F * 40F) * cos + (3F / 16F * 40F) * sin);
//        partLeg2.moveTo(posX - (5F / 16F * 40F) * sin - (3F / 16F * 40F) * cos, posY, posZ + (5F / 16F * 40F) * cos - (3F / 16F * 40F) * sin);
//        partLeg3.moveTo(posX + (7F / 16F * 40F) * sin + (3F / 16F * 40F) * cos, posY, posZ - (7F / 16F * 40F) * cos + (3F / 16F * 40F) * sin);
//        partLeg4.moveTo(posX + (7F / 16F * 40F) * sin - (3F / 16F * 40F) * cos, posY, posZ - (7F / 16F * 40F) * cos - (3F / 16F * 40F) * sin);

        partHead.setLocationAndAngles(posX - (10F / 16F * 40F) * sin, posY + (8F / 16F * 40F) + (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI)) * 16D, posZ + (10F / 16F * 40F) * cos, 0F, 0F);

        partBody.setLocationAndAngles(posX, posY + (6F / 16F * 40F), posZ, 0F, 0F);

        partLeg1.setLocationAndAngles(posX - (5F / 16F * 40F) * sin + (3F / 16F * 40F) * cos, posY, posZ + (5F / 16F * 40F) * cos + (3F / 16F * 40F) * sin, 0F, 0F);
        partLeg2.setLocationAndAngles(posX - (5F / 16F * 40F) * sin - (3F / 16F * 40F) * cos, posY, posZ + (5F / 16F * 40F) * cos - (3F / 16F * 40F) * sin, 0F, 0F);
        partLeg3.setLocationAndAngles(posX + (7F / 16F * 40F) * sin + (3F / 16F * 40F) * cos, posY, posZ - (7F / 16F * 40F) * cos + (3F / 16F * 40F) * sin, 0F, 0F);
        partLeg4.setLocationAndAngles(posX + (7F / 16F * 40F) * sin - (3F / 16F * 40F) * cos, posY, posZ - (7F / 16F * 40F) * cos - (3F / 16F * 40F) * sin, 0F, 0F);


//        for(EntityPigPart part : parts)
//        {
//            if(!part.ticked)
//            {
//                part.setLocationAndAngles(part.gotoX, part.gotoY, part.gotoZ, 0F, 0F);
//                worldObj.spawnEntityInWorld(part);
//            }
//            part.onUpdate();
//        }

        if(!worldObj.isRemote)
        {
            getBlocksInPartsNotInBB();

            if(rand.nextFloat() < (velo <= 0.0D ? 0.0075F : 0.0025F))
            {
                if(rand.nextFloat() < 0.15F)
                {
                    setIdle(true);
                    idleTimeout = 40 + rand.nextInt(20 * 20);
                    setTargetedRenderYawOffset(rand.nextFloat() * 360F);
                }
                else
                {
                    setTargetedRenderYawOffset(rand.nextFloat() * 360F);
                }
            }

            if(riddenByEntity == null && rand.nextFloat() < (watchedEntity != null ? 0.0025F : 0.010F)) //skew with watchedEntity
            {
                List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(15D, 0D, 15D).addCoord(0D, 30D, 0D).expand(30D, 0D, 30D), livingEntities);

                for(int i = 0; i < list.size(); i++)
                {
                    EntityLivingBase living = (EntityLivingBase)list.get(i);
                    if(!canSee(living))
                    {
                        continue;
                    }
                    setWatchedEntity(living);
                    break;
                }
            }

            if(riddenByEntity != null || watchedEntity != null && (!canSee(watchedEntity) || rand.nextFloat() < 0.005F))
            {
                setWatchedEntity(null);
            }
        }
        else
        {
            if(watchedEntity == null && getWatchedEntityId() != -1)
            {
                Entity ent = worldObj.getEntityByID(getWatchedEntityId());
                if(ent instanceof EntityLivingBase)
                {
                    watchedEntity = (EntityLivingBase)ent;
                }
            }
            else if(watchedEntity != null && (getWatchedEntityId() == -1 || getWatchedEntityId() != watchedEntity.getEntityId()))
            {
                watchedEntity = null;
            }
        }

        if(watchedEntity != null)
        {
            faceEntity(watchedEntity, 0.6F, 0.6F);
        }
        else if(!worldObj.isRemote || worldObj.isRemote && getWatchedEntityId() == -1)
        {
            faceEntity(null, 0.6F, 0.6F);
        }

        if(idleTimeout > 0 || getIdle())
        {
            idleTimeout--;
            motionX *= 0.8D;
            motionZ *= 0.8D;

//            System.out.println(idleTimeout);

            if(!worldObj.isRemote && idleTimeout <= 0)
            {
                setIdle(false);
            }
        }
        else
        {
            renderYawOffset = updateRotation(renderYawOffset, getTargetedRenderYawOffset(), 0.5F);

            if(riddenByEntity != null && !(riddenByEntity instanceof EntityLivingBase && ((EntityLivingBase)riddenByEntity).getHeldItem() != null && ((EntityLivingBase)riddenByEntity).getHeldItem().getItem() == Items.carrot_on_a_stick))
            {
                riddenByEntity.rotationYaw += updateRotation(renderYawOffset, getTargetedRenderYawOffset(), 0.5F) - renderYawOffset;
            }

//            if(!worldObj.isRemote)
            {
                motionX = (double)(-MathHelper.sin(renderYawOffset / 180.0F * (float)Math.PI));
                motionZ = (double)(MathHelper.cos(renderYawOffset / 180.0F * (float)Math.PI));

                float f2 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
                motionX /= (double)f2;
                motionZ /= (double)f2;
                motionX *= 0.4D;
                motionZ *= 0.4D;
            }
        }

        if(riddenByEntity != null)
        {
            rotationYaw = renderYawOffset;
            rotationPitch = 0.0F;

            if(!worldObj.isRemote && riddenByEntity instanceof EntityLivingBase)
            {
                EntityLivingBase living = (EntityLivingBase)riddenByEntity;
                ItemStack is = living.getHeldItem();
                if(is != null && is.getItem() == Items.carrot_on_a_stick && getTargetedRenderYawOffset() != living.rotationYaw)
                {
                    setTargetedRenderYawOffset(living.rotationYaw);
                    idleTimeout = 0;
                    setIdle(false);
                }
            }
        }
    }

    public boolean canSee(EntityLivingBase ent)
    {
        double viewThresh = 0.3D * Math.PI;
        return getAngle(ent.posX, ent.posY + ent.getEyeHeight(), ent.posZ) < viewThresh && ent.canEntityBeSeen(this);
    }

    public double getAngle(double d, double d1, double d2)
    {
        float f1 = MathHelper.cos(-renderYawOffset * 0.01745329F - 3.141593F);
        float f3 = MathHelper.sin(-renderYawOffset * 0.01745329F - 3.141593F);
        float f5 = -MathHelper.cos(0F * 0.01745329F);
        float f7 = MathHelper.sin(0F * 0.01745329F);

        double lookx = f3 * f5;
        double looky = f7;
        double lookz = f1 * f5;

        double dx = d - posX;
        double dy = d1 - posY - getEyeHeight();
        double dz = d2 - posZ;

        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);

        double dot = (dx / len) * lookx + (dy / len) * looky + (dz / len) * lookz;

        return Math.acos(dot);
    }

    @Override
    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.setPosition(this.posX - (Math.sin(Math.toRadians(renderYawOffset)) * 14.9D / 16F * 40D), this.posY + (12D / 16D * 40D) + this.riddenByEntity.getYOffset(), this.posZ + (Math.cos(Math.toRadians(renderYawOffset)) * 14.9D / 16F * 40D));
        }
    }

    @Override
    public boolean interactFirst(EntityPlayer entityplayer)
    {
        if (worldObj.isRemote && riddenByEntity == null)
        {
            ChannelHandler.sendToServer(new PacketRidePig(this, entityplayer));
            return true;
        }
        else if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == entityplayer))
        {
            entityplayer.mountEntity(this);
            return true;
        }
        else
        {
            return false;
        }
    }


    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
        renderYawOffset = var1.getFloat("renderYawOffset");
        setTargetedRenderYawOffset(var1.getFloat("targetedRenderYawOffset"));

        dataWatcher.updateObject(22, renderYawOffset);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
        var1.setFloat("renderYawOffset", renderYawOffset);
        var1.setFloat("targetedRenderYawOffset", getTargetedRenderYawOffset());
    }

    @Override
    public float getEyeHeight()
    {
        return 32.0F;
    }

    @Override
    public void setDead()
    {
        for(EntityPigPart part : parts)
        {
            part.setDead();
        }
        if(!worldObj.isRemote)
        {
            ChunkLoadHandler.removeTicket(this);
        }
        super.setDead();
    }

    public void faceEntity(Entity entity, float par2, float par3)
    {
        if(entity == null)
        {
            this.rotationPitch = this.updateRotation(this.rotationPitch, 0.0F, par3);
            this.rotationYaw = this.updateRotation(rotationYaw, renderYawOffset, par2);
        }
        else
        {
            double d0 = entity.posX - this.posX;
            double d2 = entity.posZ - this.posZ;
            double d1;

            if(entity instanceof EntityLivingBase)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
                d1 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
            }
            else
            {
                d1 = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
            }

            double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);
            float f2 = (float)(Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
            float f3 = (float)(-(Math.atan2(d1, d3) * 180.0D / Math.PI));
            this.rotationPitch = this.updateRotation(this.rotationPitch, f3, par3);
            this.rotationYaw = this.updateRotation(rotationYaw, f2, par2);
        }
    }

    private float updateRotation(float par1, float par2, float par3)
    {
        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (f3 > par3)
        {
            f3 = par3;
        }

        if (f3 < -par3)
        {
            f3 = -par3;
        }

        return par1 + f3;
    }

    @Override
    public void moveEntity(double par1, double par3, double par5)
    {
        if (this.noClip)
        {
            this.boundingBox.offset(par1, par3, par5);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
        }
        else
        {
            this.worldObj.theProfiler.startSection("move");
            this.ySize *= 0.4F;
            double d3 = this.posX;
            double d4 = this.posY;
            double d5 = this.posZ;

            if (this.isInWeb)
            {
                this.isInWeb = false;
                par1 *= 0.25D;
                par3 *= 0.05000000074505806D;
                par5 *= 0.25D;
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            double d6 = par1;
            double d7 = par3;
            double d8 = par5;
            AxisAlignedBB axisalignedbb = this.boundingBox.copy();

            List list = getCollidingBoundingBoxes(this.boundingBox.addCoord(par1, par3, par5));

            for (int i = 0; i < list.size(); ++i)
            {
                par3 = ((AxisAlignedBB)list.get(i)).calculateYOffset(this.boundingBox, par3);
            }

            this.boundingBox.offset(0.0D, par3, 0.0D);

            if (!this.field_70135_K && d7 != par3)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            boolean flag1 = this.onGround || d7 != par3 && d7 < 0.0D;
            int j;

            for (j = 0; j < list.size(); ++j)
            {
                par1 = ((AxisAlignedBB)list.get(j)).calculateXOffset(this.boundingBox, par1);
            }

            this.boundingBox.offset(par1, 0.0D, 0.0D);

            if (!this.field_70135_K && d6 != par1)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            for (j = 0; j < list.size(); ++j)
            {
                par5 = ((AxisAlignedBB)list.get(j)).calculateZOffset(this.boundingBox, par5);
            }

            this.boundingBox.offset(0.0D, 0.0D, par5);

            if (!this.field_70135_K && d8 != par5)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            double d10;
            double d11;
            int k;
            double d12;

            if (this.stepHeight > 0.0F && flag1 && (d6 != par1 || d8 != par5))
            {
                d12 = par1;
                d10 = par3;
                d11 = par5;
                par1 = d6;
                par3 = (double)this.stepHeight;
                par5 = d8;
                AxisAlignedBB axisalignedbb1 = this.boundingBox.copy();
                this.boundingBox.setBB(axisalignedbb);
                list = getCollidingBoundingBoxes(this.boundingBox.addCoord(d6, par3, d8));

                for (k = 0; k < list.size(); ++k)
                {
                    par3 = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, par3);
                }

                this.boundingBox.offset(0.0D, par3, 0.0D);

                if (!this.field_70135_K && d7 != par3)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                for (k = 0; k < list.size(); ++k)
                {
                    par1 = ((AxisAlignedBB)list.get(k)).calculateXOffset(this.boundingBox, par1);
                }

                this.boundingBox.offset(par1, 0.0D, 0.0D);

                if (!this.field_70135_K && d6 != par1)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                for (k = 0; k < list.size(); ++k)
                {
                    par5 = ((AxisAlignedBB)list.get(k)).calculateZOffset(this.boundingBox, par5);
                }

                this.boundingBox.offset(0.0D, 0.0D, par5);

                if (!this.field_70135_K && d8 != par5)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                if (!this.field_70135_K && d7 != par3)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }
                else
                {
                    par3 = (double)(-this.stepHeight);

                    for (k = 0; k < list.size(); ++k)
                    {
                        par3 = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, par3);
                    }

                    this.boundingBox.offset(0.0D, par3, 0.0D);
                }

                if (d12 * d12 + d11 * d11 >= par1 * par1 + par5 * par5)
                {
                    par1 = d12;
                    par3 = d10;
                    par5 = d11;
                    this.boundingBox.setBB(axisalignedbb1);
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
            this.isCollidedHorizontally = d6 != par1 || d8 != par5;
            this.isCollidedVertically = d7 != par3;
            this.onGround = d7 != par3 && d7 < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            this.updateFallState(par3, this.onGround);

            if (d6 != par1)
            {
                this.motionX = 0.0D;
            }

            if (d7 != par3)
            {
                this.motionY = 0.0D;
            }

            if (d8 != par5)
            {
                this.motionZ = 0.0D;
            }

            try
            {
                this.func_145775_I();
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }
            this.worldObj.theProfiler.endSection();
        }
    }

    @Override
    public void applyEntityCollision(Entity par1Entity)
    {
        if (par1Entity.riddenByEntity != this && par1Entity.ridingEntity != this)
        {
            double d0 = par1Entity.posX - this.posX;
            double d1 = par1Entity.posZ - this.posZ;
            double d2 = MathHelper.abs_max(d0, d1);

            if (d2 >= 0.009999999776482582D)
            {
                d2 = (double)MathHelper.sqrt_double(d2);
                d0 /= d2;
                d1 /= d2;
                double d3 = 1.0D / d2;

                if (d3 > 1.0D)
                {
                    d3 = 1.0D;
                }

                d0 *= d3;
                d1 *= d3;
                d0 *= 0.05000000074505806D;
                d1 *= 0.05000000074505806D;
                d0 *= (double)(1.0F - this.entityCollisionReduction);
                d1 *= (double)(1.0F - this.entityCollisionReduction);
                par1Entity.addVelocity(d0 * 5D, 0.3D, d1 * 5D);
            }
        }
    }

    public void getBlocksInPartsNotInBB()
    {
        List boxes = new ArrayList();

        for(int i = 0; i < parts.length; i++)
        {
            AxisAlignedBB bb = parts[i].boundingBox.copy();
            if(i > 1)
            {
                bb.offset(0D, stepHeight + 0.1D - (fallDistance / 4F), 0D);
                bb.expand(1F / 16F * 40D, 0.0D, 1F / 16F * 40D);
            }
            boxes.addAll(getCollidingBoundingBoxes(bb.addCoord(motionX, motionY, motionZ)));
        }

        for(int i = boxes.size() - 1; i >= 0; i--)
        {
            if(boxes.get(i) == null || ((AxisAlignedBB)boxes.get(i)).intersectsWith(boundingBox))
            {
                boxes.remove(i);
            }
        }

        for(int ii = 0; ii < boxes.size(); ii++)
        {
            //100 max
            boolean air = true;
            if(boxes.size() < 200 || rand.nextDouble() < 200 / boxes.size())
            {
                air = false;
            }

            AxisAlignedBB aabb = (AxisAlignedBB)boxes.get(ii);

            double psX = (aabb.minX + aabb.maxX) / 2;
            double psY = (aabb.minY + aabb.maxY) / 2;
            double psZ = (aabb.minZ + aabb.maxZ) / 2;

            int i = (int)Math.floor(psX);
            int j = (int)Math.floor(psY);
            int k = (int)Math.floor(psZ);

            Block blk = worldObj.getBlock(i, j, k);

            float blockHardness = blk.getBlockHardness(worldObj, i, j, k);
            if(!worldObj.isAirBlock(i, j, k) && blockHardness >= 0.0F && blk.canEntityDestroy(worldObj, i, j, k, this) && !(blk.getMaterial() == Material.water || blk.getMaterial() == Material.lava || FluidRegistry.lookupFluidForBlock(blk) != null))
            {
                double mX = motionX * (4.5D + (0.5D * rand.nextDouble())) + (0.25D * rand.nextDouble() - 0.25D);
                double mZ = motionZ * (4.5D + (0.5D * rand.nextDouble())) + (0.25D * rand.nextDouble() - 0.25D);
                double mY = Math.sqrt(motionX * motionX + motionZ * motionZ) + (rand.nextDouble() * 0.55D);
                if(air)
                {
                    worldObj.setBlockToAir(i, j, k);
                }
                else
                {
                    worldObj.spawnEntityInWorld(new EntityBlock(worldObj, i, j, k, mX, mY, mZ, 5));
                }
            }
        }

        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.05000000298023224D + Math.abs(motionX), 0.0D + Math.abs(motionY), 0.05000000298023224D + Math.abs(motionZ)));
        for(int kk = 0; kk < list.size(); kk++)
        {
            Entity entity = (Entity)list.get(kk);
            if(entity.canBePushed())
            {
                applyEntityCollision(entity);
            }
        }
    }

    public List getCollidingBoundingBoxes(AxisAlignedBB par2AxisAlignedBB)
    {
        return getCollidingBoundingBoxes(par2AxisAlignedBB, true);
    }

    public List getCollidingBoundingBoxes(AxisAlignedBB par2AxisAlignedBB, boolean entities)
    {
        List boxes = new ArrayList();
        int i = MathHelper.floor_double(par2AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par2AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par2AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par2AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par2AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par2AxisAlignedBB.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = i1; l1 < j1; ++l1)
            {
                if (worldObj.blockExists(k1, 64, l1))
                {
                    for (int i2 = k - 1; i2 < l; ++i2)
                    {
                        Block block;

                        if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000)
                        {
                            block = worldObj.getBlock(k1, i2, l1);
                        }
                        else
                        {
                            block = Blocks.stone;
                        }

                        block.addCollisionBoxesToList(worldObj, k1, i2, l1, par2AxisAlignedBB, boxes, this);
                    }
                }
            }
        }

        if(entities)
        {
            double d0 = 0.25D;
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, par2AxisAlignedBB.expand(d0, d0, d0));

            for(int j2 = 0; j2 < list.size(); ++j2)
            {
                if(list.get(j2) instanceof EntityPigPart)
                {
                    continue;
                }
                AxisAlignedBB axisalignedbb1 = ((Entity)list.get(j2)).getBoundingBox();

                if(axisalignedbb1 != null && axisalignedbb1.intersectsWith(par2AxisAlignedBB))
                {
                    boxes.add(axisalignedbb1);
                }

                axisalignedbb1 = this.getCollisionBox((Entity)list.get(j2));

                if(axisalignedbb1 != null && axisalignedbb1.intersectsWith(par2AxisAlignedBB))
                {
                    boxes.add(axisalignedbb1);
                }
            }
        }
        return boxes;
    }

    IEntitySelector livingEntities = new IEntitySelector()
    {
        public boolean isEntityApplicable(Entity par1Entity)
        {
            return par1Entity instanceof EntityLivingBase && par1Entity.isEntityAlive();
        }
    };

}
