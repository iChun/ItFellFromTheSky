package itfellfromthesky.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

public class EntityPigPart extends Entity
{
    public final EntityPigzilla parent;
    public final String partType;

    public boolean ticked;

    public double gotoX;
    public double gotoY;
    public double gotoZ;

    public EntityPigPart(EntityPigzilla parentObj, String type, float width, float height)
    {
        super(parentObj.worldObj);
        this.setSize(width, height);
        this.parent = parentObj;
        this.partType = type;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return par1Entity.boundingBox;
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
        return this.boundingBox;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate()
    {
        ticked = true;

        //        moveEntity(gotoX - posX, gotoY - posY, gotoZ - posZ);

        if(partType.startsWith("leg"))
        {

        }
    }

    public void moveTo(double d, double d1, double d2)
    {
        gotoX = d;
        gotoY = d1;
        gotoZ = d2;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        return this.isEntityInvulnerable() ? false : this.parent.attackEntityFrom(par1DamageSource, par2);
    }

    /**
     * Returns true if Entity argument is equal to this Entity
     */
    public boolean isEntityEqual(Entity par1Entity)
    {
        return this == par1Entity || this.parent == par1Entity;
    }
}