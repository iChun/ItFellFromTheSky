package itfellfromthesky.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityTransformer extends Entity
{

    public float prevRotYaw;
    public float prevRotPitch;
    public float rotYaw;
    public float rotPitch;

    public int waitPeriod;

    public EntityMeteorite parent;

    public int transformationProcess;

    public static int transformationTime = 10 * 20; //10 seconds?

    public EntityTransformer(World world)
    {
        super(world);

        renderDistanceWeight = 60D;
        ignoreFrustumCheck = true;
    }

    public EntityTransformer(EntityMeteorite meteorite, int timeout)
    {
        this(meteorite.worldObj);

        waitPeriod = timeout;

        setSize(meteorite.width, meteorite.height);
        dataWatcher.updateObject(20, meteorite.getEntityId());
        dataWatcher.updateObject(22, 180F - (float)Math.toDegrees(Math.atan2(meteorite.posX - meteorite.originX, meteorite.posZ - meteorite.originZ)));

        setLocationAndAngles(meteorite.posX, meteorite.posY, meteorite.posZ, meteorite.rotationYaw, meteorite.rotationPitch);
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(20, -1);//meteor ID
        dataWatcher.addObject(21, (byte)0);//canTransform
        dataWatcher.addObject(22, 0F);//body rotation
        dataWatcher.addObject(23, 0);
    }

    public float getOriginRot()
    {
        return dataWatcher.getWatchableObjectFloat(22);
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

    @Override
    public void onUpdate()
    {
        rotYaw = rotYaw % 360F;
        rotPitch = rotPitch % 360F;

        if(worldObj.isRemote && ticksExisted < 5 && parent == null && dataWatcher.getWatchableObjectInt(20) != -1)
        {
            if(ticksExisted == 1)
            {
                transformationProcess = dataWatcher.getWatchableObjectInt(23);
            }
            for(int i = 0; i < worldObj.weatherEffects.size(); i++)
            {
                Entity ent = (Entity)worldObj.weatherEffects.get(i);
                if(ent instanceof EntityMeteorite && ent.getEntityId() == dataWatcher.getWatchableObjectInt(20))
                {
                    parent = ((EntityMeteorite)ent);
                    prevRotYaw = parent.prevRotYaw;
                    prevRotPitch = parent.prevRotPitch;
                    rotYaw = parent.rotYaw;
                    rotPitch = parent.rotPitch;
                    break;
                }
            }
        }
        prevRotYaw = rotYaw;
        prevRotPitch = rotPitch;


        if(waitPeriod > 0)
        {
            waitPeriod--;
            if(waitPeriod == 0 && !worldObj.isRemote)
            {
                dataWatcher.updateObject(21, (byte)1);
            }
        }
        else if(!worldObj.isRemote || (dataWatcher.getWatchableObjectByte(21) & 1) != 0)
        {
            transformationProcess++;

            if(transformationProcess > transformationTime / 2 + 20)
            {
                rotYaw = getOriginRot() + (rotYaw - getOriginRot()) * 0.9F;
                rotPitch *= 0.9F;
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
        rotYaw = var1.getFloat("rotYaw");
        rotPitch = var1.getFloat("rotPitch");

        transformationProcess = var1.getInteger("transformationProcess");

        dataWatcher.updateObject(23, transformationProcess);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
        var1.setFloat("rotYaw", rotYaw);
        var1.setFloat("rotPitch", rotPitch);
        var1.setInteger("transformationProcess", transformationProcess);
    }
}
