package itfellfromthesky.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityMeteorite extends Entity
{

    public float rotYaw;
    public float rotPitch;
    public float prevRotYaw;
    public float prevRotPitch;

    public static float maxRotFac = 10F;

    public EntityMeteorite(World world)
    {
        super(world);

        setSize(23F, 23F);

        yOffset = height / 2F;

        preventEntitySpawning = true;
        ignoreFrustumCheck = true;
        renderDistanceWeight = 60D;
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

    @Override
    public AxisAlignedBB getBoundingBox()
    {
        return boundingBox.expand(-3D, -3D, -3D);
    }

    @Override
    public void onUpdate()
    {
        if(worldObj.isRemote && ticksExisted == 1)
        {
            lastTickPosY -= yOffset;
            prevPosY -= yOffset;
            posY -= yOffset;
            setPosition(posX, posY, posZ);
        }

        prevRotYaw = rotYaw;
        prevRotPitch = rotPitch;
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        rotYaw += getRotFacYaw();
        rotPitch += getRotFacPitch();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
    }
}
