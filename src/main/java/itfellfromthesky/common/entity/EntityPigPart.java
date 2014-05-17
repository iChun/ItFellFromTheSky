package itfellfromthesky.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityPigPart extends Entity
{
    public final EntityPigzilla parent;
    public final String partType;

    public EntityPigPart(EntityPigzilla parentObj, String type, float width, float height)
    {
        super(parentObj.worldObj);
        this.setSize(width, height);
        this.parent = parentObj;
        this.partType = type;
    }

    protected void entityInit() {}

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Called when the entity is attacked.
     */
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