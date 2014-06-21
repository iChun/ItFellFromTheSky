package itfellfromthesky.common.entity;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import itfellfromthesky.common.core.ObfHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//TODO extend IInventory??
public class EntityBlock extends Entity
{
    public float rotYaw;
    public float rotPitch;
    public float prevRotYaw;
    public float prevRotPitch;

    public double prevMotionX;
    public double prevMotionY;
    public double prevMotionZ;

    public int timeOnGround;

    public int timeExisting;

    public Block block;

    public NBTTagCompound tileEntityNBT;

    public int timeout;

    public static float maxRotFac = 25F;

    public EntityBlock(World world)
    {
        super(world);
        setSize(0.95F, 0.95F);

        yOffset = height / 2F;

        preventEntitySpawning = true;
        renderDistanceWeight = 20D;
        //        motionX = 1D;
        //        motionY = motionZ = 1D;
    }

    public EntityBlock(World world, int i, int j, int k)
    {
        this(world);

        setBlock(world.getBlock(i, j, k));
        setMeta(world.getBlockMetadata(i, j, k));

        TileEntity te = world.getTileEntity(i, j, k);
        if(te != null && block instanceof ITileEntityProvider)
        {
            world.setTileEntity(i, j, k, ((ITileEntityProvider)block).createNewTileEntity(world, getMeta()));

            tileEntityNBT = new NBTTagCompound();
            te.writeToNBT(tileEntityNBT);

            te.invalidate();
        }

        world.setBlockToAir(i, j, k);

        setLocationAndAngles(i + 0.5D, j + 0.5D - (double)yOffset, k + 0.5D, 0F, 0F);
    }

    public EntityBlock(World world, int i, int j, int k, double mX, double mY, double mZ, int time)
    {
        this(world, i, j, k);

        motionX = mX;
        motionY = mY;
        motionZ = mZ;

        timeout = time;
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(16, Block.getIdFromBlock(Blocks.bedrock));//blockID
        dataWatcher.addObject(17, 0);//metadata

        dataWatcher.addObject(18, rand.nextFloat() * (2F * maxRotFac) - maxRotFac); //rotFactor Yaw
        dataWatcher.addObject(19, rand.nextFloat() * (2F * maxRotFac) - maxRotFac); //rotFactor Pitch
    }

    public void setBlock(Block block)
    {
        this.block = block;
        dataWatcher.updateObject(16, Block.getIdFromBlock(block));
    }

    public Block getBlock()
    {
        if(block == null)
        {
            block = Block.getBlockById(dataWatcher.getWatchableObjectInt(16));
        }
        return block == null ? Blocks.bedrock : block;
    }

    public void setMeta(int i)
    {
        dataWatcher.updateObject(17, i);
    }

    public int getMeta()
    {
        return dataWatcher.getWatchableObjectInt(17);
    }

    public void setRotFacYaw(float f)
    {
        dataWatcher.updateObject(18, f);
    }

    public float getRotFacYaw()
    {
        return dataWatcher.getWatchableObjectFloat(18);
    }

    public void setRotFacPitch(float f)
    {
        dataWatcher.updateObject(19, f);
    }

    public float getRotFacPitch()
    {
        return dataWatcher.getWatchableObjectFloat(19);
    }

    @Override
    public void onUpdate()
    {
        timeExisting++;
        if(!worldObj.isRemote && (block == null || block.equals(Blocks.bedrock)) || posY < -50D)
        {
            setDead();
            return;
        }
        if(worldObj.isRemote && ticksExisted == 1)
        {
            lastTickPosY -= yOffset;
            prevPosY -= yOffset;
            posY -= yOffset;
            setPosition(posX, posY, posZ);
        }

        if(timeExisting < timeout)
        {
            noClip = true;
        }
        else
        {
            noClip = false;
        }

        prevRotYaw = rotYaw;
        prevRotPitch = rotPitch;
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        rotYaw += getRotFacYaw();
        rotPitch += getRotFacPitch();

        motionY -= 0.04D;

        prevMotionX = motionX;
        prevMotionY = motionY;
        prevMotionZ = motionZ;

        moveEntity(motionX, motionY, motionZ);

        boolean setBlock = false;

        if(onGround && ticksExisted > 2)
        {
            timeOnGround++;
            if(motionY == 0.0D)
            {
                if(prevMotionY < -0.1D)
                {
                    double minBounceFactor = Math.sqrt(100D / 75D);
                    float blockHardness = (Float)ObfuscationReflectionHelper.getPrivateValue(Block.class, getBlock(), ObfHelper.blockHardness);
                    double bounceFactor = (blockHardness < minBounceFactor ? minBounceFactor : blockHardness);
                    motionY = prevMotionY * -(1D / (2 * bounceFactor));

                    setRotFacYaw(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
                    setRotFacPitch(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
                }
                if(timeOnGround > 3)
                {
                    setRotFacYaw(getRotFacYaw() * 0.6F);
                    setRotFacPitch(getRotFacPitch() * 0.6F);
                }
                motionX *= 0.7D;
                motionZ *= 0.7D;

                if(Math.abs(prevMotionX) < 0.05D && Math.abs(prevMotionZ) < 0.05D && Math.abs(prevMotionY) < 0.05D && Math.abs(getRotFacYaw()) < 0.05D && Math.abs(getRotFacPitch()) < 0.05D)
                {
                    setBlock = true;
                }
            }
        }
        else
        {
            timeOnGround = 0;
        }

        if(motionX == 0D && prevMotionX != motionX)
        {
            double minBounceFactor = Math.sqrt(100D / 75D);
            float blockHardness = (Float)ObfuscationReflectionHelper.getPrivateValue(Block.class, getBlock(), ObfHelper.blockHardness);
            double bounceFactor = 2D * (blockHardness < minBounceFactor ? minBounceFactor : blockHardness);
            motionX = prevMotionX * -(1D / (bounceFactor * bounceFactor));

            setRotFacYaw(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
            setRotFacPitch(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
        }
        if(motionZ == 0D && prevMotionZ != motionZ)
        {
            double minBounceFactor = Math.sqrt(100D / 75D);
            float blockHardness = (Float)ObfuscationReflectionHelper.getPrivateValue(Block.class, getBlock(), ObfHelper.blockHardness);
            double bounceFactor = 2D * (blockHardness < minBounceFactor ? minBounceFactor : blockHardness);
            motionZ = prevMotionZ * -(1D / (bounceFactor * bounceFactor));

            setRotFacYaw(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
            setRotFacPitch(rand.nextFloat() * (2F * maxRotFac) - maxRotFac);
        }

        if(!worldObj.isRemote && (setBlock || timeExisting > (20 * 60 * 5)))
        {
            setDead();

            int i = (int)Math.floor(posX);
            int j = (int)Math.floor(posY);
            int k = (int)Math.floor(posZ);

            worldObj.setBlock(i, j, k, getBlock(), getMeta(), 3);

            if(tileEntityNBT != null && getBlock() instanceof ITileEntityProvider)
            {
                TileEntity te = worldObj.getTileEntity(i, j, k);

                if (te != null)
                {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    te.writeToNBT(nbttagcompound);
                    Iterator iterator = tileEntityNBT.func_150296_c().iterator();

                    while (iterator.hasNext())
                    {
                        String s = (String)iterator.next();
                        NBTBase nbtbase = tileEntityNBT.getTag(s);

                        if (!s.equals("x") && !s.equals("y") && !s.equals("z"))
                        {
                            nbttagcompound.setTag(s, nbtbase.copy());
                        }
                    }

                    te.readFromNBT(nbttagcompound);
                    te.markDirty();
                }
            }

            if(!(getBlock() instanceof BlockChest))
            {
                worldObj.setBlockMetadataWithNotify(i, j, k, getMeta(), 2);
            }
        }

        motionX *= 0.9875D;
        motionY *= 0.9875D;
        motionZ *= 0.9875D;

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

                        block.addCollisionBoxesToList(worldObj, k1, i2, l1, par2AxisAlignedBB, boxes, this);
                    }
                }
            }
        }

        double d0 = 0.25D;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, par2AxisAlignedBB.expand(d0, d0, d0));

        for (int j2 = 0; j2 < list.size(); ++j2)
        {
            if(list.get(j2) instanceof EntityMeteorite)
            {
                continue;
            }
            AxisAlignedBB axisalignedbb1 = ((Entity)list.get(j2)).getBoundingBox();

            if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(par2AxisAlignedBB))
            {
                boxes.add(axisalignedbb1);
            }

            axisalignedbb1 = this.getCollisionBox((Entity)list.get(j2));

            if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(par2AxisAlignedBB))
            {
                boxes.add(axisalignedbb1);
            }
        }

        return boxes;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return timeExisting > timeout && !isDead;
    }

    @Override
    public boolean canBePushed()
    {
        return timeExisting > timeout && !isDead;
    }

    @Override
    protected void dealFireDamage(int par1)
    {
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        return entity.boundingBox;
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag)
    {
        setBlock(Block.getBlockById(tag.getInteger("block")));
        setMeta(tag.getInteger("meta"));

        timeExisting = tag.getInteger("timeExisting");

        tileEntityNBT = tag.getCompoundTag("tileEntity");
        if(tileEntityNBT.equals(new NBTTagCompound()))
        {
            tileEntityNBT = null;
        }
    }

    //TODO Read block getUnlocalizedName()..? Prevents mod issues.
    @Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
        tag.setInteger("block", Block.getIdFromBlock(getBlock()));
        tag.setInteger("meta", getMeta());
        tag.setInteger("timeExisting", timeExisting);

        if(tileEntityNBT != null)
        {
            tag.setTag("tileEntity", tileEntityNBT);
        }
    }

    @Override
    public float getShadowSize()
    {
        return 0.0F;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderOnFire()
    {
        return false;
    }
}
