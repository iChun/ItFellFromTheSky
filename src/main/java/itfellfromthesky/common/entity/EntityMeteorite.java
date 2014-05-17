package itfellfromthesky.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import itfellfromthesky.common.core.ChunkLoadHandler;
import itfellfromthesky.common.network.ChannelHandler;
import itfellfromthesky.common.network.PacketKillMeteorite;
import itfellfromthesky.common.network.PacketMeteoriteInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.List;

public class EntityMeteorite extends Entity
{

    public float rotYaw;
    public float rotPitch;
    public float prevRotYaw;
    public float prevRotPitch;

    public boolean canSetDead;

    public boolean stopped;

    public double prevInertia;

    public int stopTime;

    public double originX;
    public double originZ;

    public static float maxRotFac = 10F;

    public EntityMeteorite(World world)
    {
        super(world);

        setSize(23F, 23F);

        yOffset = height / 2F;

        preventEntitySpawning = true;
        ignoreFrustumCheck = true;
        renderDistanceWeight = 60D;

//        motionX = 1D;
//        motionZ = -1D;
//        motionX = rand.nextDouble() * 2 - 1D;
//        motionZ = rand.nextDouble() * 2 - 1D;
        motionY = -0.1D;
    }

    public EntityMeteorite(World world, double x, double y, double z)
    {
        this(world);

        originX = x;
        originZ = z;
        setLocationAndAngles(x, y, z, 0F, 0F);
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(18, rand.nextFloat() * (2F * maxRotFac) - maxRotFac); //rotFactor Yaw
        dataWatcher.addObject(19, rand.nextFloat() * (2F * maxRotFac) - maxRotFac); //rotFactor Pitch

        dataWatcher.addObject(20, 0F); //motionX
        dataWatcher.addObject(21, 0F); //motionY
        dataWatcher.addObject(22, 0F); //motionZ

        dataWatcher.addObject(23, 0F); //Initial yaw
        dataWatcher.addObject(24, 0F); //Initial pitch
    }

    public void setRotFacYaw(float f)
    {
        if(getRotFacYaw() != f)
        {
            dataWatcher.updateObject(18, f);
        }
    }

    public float getRotFacYaw()
    {
        return dataWatcher.getWatchableObjectFloat(18);
    }

    public void setRotFacPitch(float f)
    {
        if(getRotFacPitch() != f)
        {
            dataWatcher.updateObject(19, f);
        }
    }

    public float getRotFacPitch()
    {
        return dataWatcher.getWatchableObjectFloat(19);
    }

    public void updateMotion(double d, double d1, double d2)
    {
        if(!(getMoX() == d && getMoY() == d1 && getMoZ() == d2))
        {
            dataWatcher.updateObject(20, (float)d);
            dataWatcher.updateObject(21, (float)d1);
            dataWatcher.updateObject(22, (float)d2);
        }
    }

    public double getMoX()
    {
        return (double)dataWatcher.getWatchableObjectFloat(20);
    }

    public double getMoY()
    {
        return (double)dataWatcher.getWatchableObjectFloat(21);
    }

    public double getMoZ()
    {
        return (double)dataWatcher.getWatchableObjectFloat(22);
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
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

//    @Override
//    public AxisAlignedBB getBoundingBox()
//    {
//        return boundingBox.expand(-3D, -3D, -3D);
//    }

    //TODO tell forge that killing a weather event somehow causes a EntityJoinedWorldEvent
    @Override
    public void onUpdate()
    {
//        if(ticksExisted > 2000)
//        setDead();
        if(worldObj.isRemote && ticksExisted == 1)
        {
            lastTickPosY -= yOffset;
            prevPosY -= yOffset;
            posY -= yOffset;
            setPosition(posX, posY, posZ);
        }

        if(posY < -64D)
        {
            setDead();
            return;
        }

        prevRotYaw = rotYaw;
        prevRotPitch = rotPitch;
        lastTickPosX = prevPosX = posX;
        lastTickPosY = prevPosY = posY;
        lastTickPosZ = prevPosZ = posZ;

        rotYaw += getRotFacYaw();
        rotPitch += getRotFacPitch();

//        this.boundingBox.offset(motionX, motionY, motionZ);
//        this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
//        this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
//        this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;

        if(!worldObj.isRemote)
        {
            moveEntity(motionX, motionY, motionZ);

            double degs = (double)Math.atan2(motionX, motionZ);

            double radius = (double)width / 2D * 1.1D;
            double halfHeight = (double)height / 2D * 1.1D;

            double radius2 = radius;

            if(prevInertia - 0.07D > 0.0D)
            {
                double amp = MathHelper.clamp_double(1.0D + ((prevInertia - 0.07D) / 1.2D), 1.0D, 1.3D);
                radius2 *= amp;
            }

            double inertiaFactor = 0.0001D;

            double inertiaDampening = 0.0D;

            double backChance = radius / radius2;

            for(double y = posY - halfHeight; y <= posY + halfHeight; y++)
            {
                double x1 = posX - ((int)Math.round(Math.cos(degs) * radius * (1.0D - MathHelper.clamp_double((Math.abs(y - posY)) / halfHeight, 0.0D, 1.0D))));
                double x2 = posX + ((int)Math.round(Math.cos(degs) * radius * (1.0D - MathHelper.clamp_double((Math.abs(y - posY)) / halfHeight, 0.0D, 1.0D))));

                double z1 = posZ + ((int)Math.round(Math.sin(degs) * radius * (1.0D - MathHelper.clamp_double((Math.abs(y - posY)) / halfHeight, 0.0D, 1.0D))));
                double z2 = posZ - ((int)Math.round(Math.sin(degs) * radius * (1.0D - MathHelper.clamp_double((Math.abs(y - posY)) / halfHeight, 0.0D, 1.0D))));

                if(x1 > x2)
                {
                    double xx = x2;
                    x2 = x1;
                    x1 = xx;
                }
                if(z1 > z2)
                {
                    double zz = z2;
                    z2 = z1;
                    z1 = zz;
                }

                double x3 = posX + ((int)Math.round(Math.sin(degs) * radius * (1.0D - MathHelper.clamp_double((Math.abs(y - posY)) / halfHeight, 0.0D, 1.0D))));
                double x4 = posX;

                double z3 = posZ + ((int)Math.round(Math.cos(degs) * radius * (1.0D - MathHelper.clamp_double((Math.abs(y - posY)) / halfHeight, 0.0D, 1.0D))));
                double z4 = posZ;

                if(x3 > x4)
                {
                    double xx = x4;
                    x4 = x3;
                    x3 = xx;
                }
                if(z3 > z4)
                {
                    double zz = z4;
                    z4 = z3;
                    z3 = zz;
                }

                if(x3 < x1)
                {
                    x1 = x3;
                }
                if(x4 > x2)
                {
                    x2 = x4;
                }
                if(z3 < z1)
                {
                    z1 = z3;
                }
                if(z4 > z2)
                {
                    z2 = z4;
                }

                for(double x = x1; x <= x2; x++)
                {
                    for(double z = z1; z <= z2; z++)
                    {
                        int i = (int)Math.floor(x);
                        int j = (int)Math.floor(y);
                        int k = (int)Math.floor(z);
                        Block blk = worldObj.getBlock(i, j, k);

                        float blockHardness = blk.getBlockHardness(worldObj, i, j, k);
                        if(getDistance(x, y, z) < radius && !worldObj.isAirBlock(i, j, k) && blockHardness >= 0.0F && blk.canEntityDestroy(worldObj, i, j, k, this))
                        {
                            boolean isFluid = false;
                            if(blk.getMaterial() == Material.water || blk.getMaterial() == Material.lava || FluidRegistry.lookupFluidForBlock(blk) != null)
                            {
                                isFluid = true;
                                blockHardness = 1.0F;
                            }
                            if(blockHardness > 100.0F)
                            {
                                blockHardness *= 0.25F;
                            }
                            if(blockHardness > 10.0F)
                            {
                                blockHardness *= 0.5F;
                            }
                            if(blockHardness > 1.0F)
                            {
                                blockHardness *= 0.75F;
                            }
                            inertiaDampening = (1.01D * inertiaDampening) + (blockHardness * blockHardness * inertiaFactor);

                            if(rand.nextFloat() < 0.25F && !isFluid)
                            {
                                double amp = 0.75D;

                                double offsetX = 0.0D;
                                double offsetZ = 0.0D;

                                double xDist = Math.abs(posX - x);
                                if(x >= posX && x <= x2 + 1)
                                {
                                    offsetX = amp * Math.pow(xDist / (x2 - posX), 2);
                                }
                                else if(x > x1 - 1)
                                {
                                    offsetX = -amp * Math.pow(xDist / (posX - x1), 2);
                                }

                                double zDist = Math.abs(posZ - z);
                                if(z >= posZ && z <= z2 + 1)
                                {
                                    offsetZ = amp * Math.pow(zDist / (z2 - posZ), 2);
                                }
                                else if(z > z1 - 1)
                                {
                                    offsetZ = -amp * Math.pow(zDist / (posZ - z1), 2);
                                }

                                double mX = motionX * (1.5D + (0.5D * rand.nextDouble())) + offsetX;
                                double mZ = motionZ * (1.5D + (0.5D * rand.nextDouble())) + offsetZ;
                                double mY = Math.sqrt(motionX * motionX + motionZ * motionZ) + (rand.nextDouble() * 0.5D);

                                if(backChance != 1.0D)
                                {
                                    mX = motionX * (2D + (0.5D * rand.nextDouble())) * -1D + offsetX * 0.5D;
                                    mZ = motionZ * (2D + (0.5D * rand.nextDouble())) * -1D + offsetZ * 0.5D;
                                    mY = Math.sqrt(motionX * motionX + motionZ * motionZ) * 0.5D + (rand.nextDouble() * 0.5D);
                                }
                                //TODO perpendicular motion?
                                worldObj.spawnEntityInWorld(new EntityBlock(worldObj, i, j, k, mX, mY, mZ, 30));
                            }
                            else
                            {
                                worldObj.setBlockToAir(i, j, k);
                            }
                        }

//                        worldObj.setBlock(i - ((int)Math.round(Math.cos(degs) * radius)), j, k + ((int)Math.round(Math.sin(degs) * radius)), Blocks.leaves, 0, 3);
//                        worldObj.setBlock(i + ((int)Math.round(Math.cos(degs) * radius)), j, k - ((int)Math.round(Math.sin(degs) * radius)), Blocks.leaves, 0, 3);
//
//                        worldObj.setBlock(i + ((int)Math.round(Math.sin(degs) * radius)), j, k + ((int)Math.round(Math.cos(degs) * radius)), Blocks.leaves, 0, 3);
                    }
                }
            }

            prevInertia = inertiaDampening;

            motionX *= 1.0D - inertiaDampening;
            motionZ *= 1.0D - inertiaDampening;
            motionY *= 1.0D - inertiaDampening;

            setRotFacYaw(getRotFacYaw() * (float)(1.0D - (2 * inertiaDampening)));
            setRotFacPitch(getRotFacPitch() * (float)(1.0D - (2 * inertiaDampening)));

            if(motionY <= -0.1D)
            {
                motionY += inertiaDampening > 0.04 ? 0.04 : inertiaDampening;
            }

            if(inertiaDampening > 0.0D)
            {
                motionY -= 0.001D;
            }

            double velo = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            if(velo < 0.333D)
            {
                motionX *= 0.8D;
                motionY *= 0.8D;
                motionZ *= 0.8D;

                setRotFacYaw(getRotFacYaw() * 0.85F);
                setRotFacPitch(getRotFacPitch() * 0.85F);

                if(velo < 0.01D)
                {
                    stopped = true;
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

            updateMotion(motionX, motionY, motionZ);

            updateDataWatcher();
        }
        else
        {
            moveEntity(getMoX(), getMoY(), getMoZ());
//            System.out.println(posY);
        }

        if(stopped)
        {
            setRotFacYaw(0F);
            setRotFacPitch(0F);
            motionX = motionY = motionZ = 0.0D;

            stopTime++;
            if(!worldObj.isRemote)
            {
                if(stopTime == 20)
                {
                    EntityTransformer trans = new EntityTransformer(this, 60 + rand.nextInt(140));
                    worldObj.spawnEntityInWorld(trans);
                    ChunkLoadHandler.passChunkloadTicket(this, trans);
                }
                if(stopTime == 25)
                {
                    setDead();
                }
            }
        }
    }

    private void updateDataWatcher()
    {
        if(dataWatcher.hasChanges())
        {
            ChannelHandler.sendToDimension(new PacketMeteoriteInfo(this), worldObj.provider.dimensionId);
            dataWatcher.getChanged();
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
                par1Entity.addVelocity(d0 * 7.5D, 0.5D, d1 * 7.5D);
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
        rotYaw = var1.getFloat("rotYaw");
        rotPitch = var1.getFloat("rotPitch");

        dataWatcher.updateObject(23, rotYaw);
        dataWatcher.updateObject(24, rotPitch);

        originX = var1.getDouble("originX");
        originZ = var1.getDouble("originZ");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
        var1.setFloat("rotYaw", rotYaw);
        var1.setFloat("rotPitch", rotPitch);

        var1.setDouble("originX", originX);
        var1.setDouble("originZ", originZ);
    }

    @Override
    public void setDead()
    {
        if(!worldObj.isRemote)
        {
            ChannelHandler.sendToDimension(new PacketKillMeteorite(getEntityId()), worldObj.provider.dimensionId);
            ChunkLoadHandler.removeTicket(this);
            super.setDead();
        }
        if(worldObj.isRemote && canSetDead)
        {
            super.setDead();
        }
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

            if (this.stepHeight > 0.0F && flag1 && this.ySize < 0.05F && (d6 != par1 || d8 != par5))
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

            d12 = this.posX - d3;
            d10 = this.posY - d4;
            d11 = this.posZ - d5;

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

            boolean flag2 = this.isWet();

            this.worldObj.theProfiler.endSection();
        }
    }

    public List getCollidingBoundingBoxes(AxisAlignedBB par2AxisAlignedBB)
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

                        if(block.getBlockHardness(worldObj, k1, i2, l1) < 0.0D)
                        {
                            block.addCollisionBoxesToList(worldObj, k1, i2, l1, par2AxisAlignedBB, boxes, this);
                        }
                    }
                }
            }
        }

        return boxes;
    }

}
