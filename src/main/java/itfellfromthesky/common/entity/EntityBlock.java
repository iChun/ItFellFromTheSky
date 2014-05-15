package itfellfromthesky.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

//TODO extend IInventory??
public class EntityBlock extends Entity
{
    public float rotationRoll;

    public float rotFacYaw;
    public float rotFacPitch;
    public float rotFacRoll;

    public Block block;

    public static float maxRotFac = 60F;

    public EntityBlock(World world)
    {
        super(world);

        rotationRoll = 0.0F;

        rotFacYaw = rand.nextFloat() * (2F * maxRotFac) - maxRotFac;
        rotFacPitch = rand.nextFloat() * (2F * maxRotFac) - maxRotFac;
        rotFacRoll = rand.nextFloat() * (2F * maxRotFac) - maxRotFac;

        setSize(0.95F, 0.95F);

        preventEntitySpawning = true;
        renderDistanceWeight = 20D;
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(16, Block.getIdFromBlock(Blocks.bedrock));//blockID
        dataWatcher.addObject(17, 0);//metadata
    }

    public void setBlock(Block block)
    {
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

    @Override
    public void onUpdate()
    {
        
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag)
    {
        setBlock(Block.getBlockById(tag.getInteger("block")));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
        tag.setInteger("block", Block.getIdFromBlock(getBlock()));
        tag.setInteger("meta", getMeta());
    }

    @Override
    public float getShadowSize()
    {
        return 0.0F;
    }

    //TODO rethink this
    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderOnFire()
    {
        return false;
    }
}
