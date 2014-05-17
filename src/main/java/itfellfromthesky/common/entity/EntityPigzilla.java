package itfellfromthesky.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityPigzilla extends Entity
{

    public EntityPigPart[] parts;
    public EntityPigPart partHead;
    public EntityPigPart partBody;
    public EntityPigPart partLeg1;
    public EntityPigPart partLeg2;
    public EntityPigPart partLeg3;
    public EntityPigPart partLeg4;

    public EntityPigzilla(World world)
    {
        super(world);
        parts = new EntityPigPart[] { partHead = new EntityPigPart(this, "head", 8F / 16F * 40F, 8F / 16F * 40F), partBody = new EntityPigPart(this, "body", 12F / 16F * 40F, 8F / 16F * 40F), partLeg1 = new EntityPigPart(this, "leg1", 4F / 16F * 40F, 6F / 16F * 40F), partLeg2 = new EntityPigPart(this, "leg2", 4F / 16F * 40F, 6F / 16F * 40F), partLeg3 = new EntityPigPart(this, "leg3", 4F / 16F * 40F, 6F / 16F * 40F), partLeg4 = new EntityPigPart(this, "leg4", 4F / 16F * 40F, 6F / 16F * 40F)};
        setSize(40F, 40F);

        renderDistanceWeight = 60D;
        ignoreFrustumCheck = true;

        for(EntityPigPart part : parts)
        {
            world.spawnEntityInWorld(part);
        }
    }

    public EntityPigzilla(World world, EntityTransformer trans)
    {
        this(world);
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public boolean canBeCollidedWith()
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

    @Override
    public void onUpdate()
    {
        rotationYaw++;
        for(EntityPigPart part : parts)
        {
            part.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
        }

        double yawRad = Math.toRadians(rotationYaw);

        double sin = Math.sin(yawRad);
        double cos = Math.cos(yawRad);

        partHead.setLocationAndAngles(posX - (10F / 16F * 40F) * sin, posY + (8F / 16F * 40F), posZ + (10F / 16F * 40F) * cos, 0F, 0F);

//        double x3 = posX + ((int)Math.round(Math.sin(degs) * radius * (1.0D - MathHelper.clamp_double((Math.abs(y - posY)) / halfHeight, 0.0D, 1.0D))));
//        double x4 = posX;
//
//        double z3 = posZ + ((int)Math.round(Math.cos(degs) * radius * (1.0D - MathHelper.clamp_double((Math.abs(y - posY)) / halfHeight, 0.0D, 1.0D))));


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
