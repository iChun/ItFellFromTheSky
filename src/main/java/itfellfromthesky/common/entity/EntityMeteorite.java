package itfellfromthesky.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import itfellfromthesky.common.network.ChannelHandler;
import itfellfromthesky.common.network.PacketKillMeteorite;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMeteorite extends Entity
{

    public float rotYaw;
    public float rotPitch;
    public float prevRotYaw;
    public float prevRotPitch;

    public boolean canSetDead;

    public static float maxRotFac = 10F;

    public EntityMeteorite(World world)
    {
        super(world);

        setSize(23F, 23F);

        yOffset = height / 2F;

        preventEntitySpawning = true;
        ignoreFrustumCheck = true;
        renderDistanceWeight = 60D;

        motionX = 1D;
//        motionZ = -1D;
//        motionX = rand.nextDouble() * 2 - 1D;
//        motionZ = rand.nextDouble() * 2 - 1D;
        motionY = -0.1D;
    }

    public EntityMeteorite(World world, double x, double y, double z)
    {
        this(world);

        setLocationAndAngles(x, y, z, 0F, 0F);
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(18, rand.nextFloat() * (2F * maxRotFac) - maxRotFac); //rotFactor Yaw
        dataWatcher.addObject(19, rand.nextFloat() * (2F * maxRotFac) - maxRotFac); //rotFactor Pitch
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
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }

    @Override
    public boolean canBePushed()
    {
        return !isDead;
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
//        if(ticksExisted > 60)
//        setDead();
        noClip = true;
        if(worldObj.isRemote && ticksExisted == 1)
        {
            lastTickPosY -= yOffset;
            prevPosY -= yOffset;
            posY -= yOffset;
            setPosition(posX, posY, posZ);
        }

        prevRotYaw = rotYaw;
        prevRotPitch = rotPitch;
        lastTickPosX = prevPosX = posX;
        lastTickPosY = prevPosY = posY;
        lastTickPosZ = prevPosZ = posZ;

        rotYaw += getRotFacYaw();
        rotPitch += getRotFacPitch();

        moveEntity(motionX, motionY, motionZ);

//        Math.toDegrees(Math.atan2(motionZ, motionX));

        if(!worldObj.isRemote)
        {
            double degs = (double)Math.atan2(motionX, motionZ);

            double radius = (double)width / 2D * 1.1D;
            double halfHeight = (double)height / 2D * 1.1D;

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

                for(double x = x1; x <= x2; x++)
                {
                    for(double z = z1; z <= z2; z++)
                    {
                        int i = (int)Math.floor(x);
                        int j = (int)Math.floor(y);
                        int k = (int)Math.floor(z);

                        if(!worldObj.isAirBlock(i, j, k))
                        {
                            if(rand.nextFloat() < 0.5F)
                            {
                                double mX = motionX + ((rand.nextDouble() - 0.5D) * 0.4D) * 1.3D;
                                double mZ = motionZ + ((rand.nextDouble() - 0.5D) * 0.4D) * 1.3D;
                                //TODO perpendicular motion?
                                double mY = 0.4D + (rand.nextDouble() * 0.7D);
                                worldObj.spawnEntityInWorld(new EntityBlock(worldObj, i, j, k, mX, mY, mZ));
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
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
    }

    @Override
    public void setDead()
    {
        if(!worldObj.isRemote)
        {
            ChannelHandler.sendToDimension(new PacketKillMeteorite(getEntityId()), worldObj.provider.dimensionId);
        }
        if(worldObj.isRemote && canSetDead || !worldObj.isRemote)
        {
            super.setDead();
        }
    }
}
