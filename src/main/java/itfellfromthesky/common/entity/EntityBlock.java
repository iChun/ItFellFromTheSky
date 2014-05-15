package itfellfromthesky.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

//TODO extend IInventory??
public class EntityBlock extends Entity
{
    public float rotationRoll;

    public float rotFacYaw;
    public float rotFacPitch;
    public float rotFacRoll;

    public Block block;

    //TODO remember to set this when reading a tile's TE.
    //TODO remember to set new XYZ positions in the tag before spawning.
    public NBTTagCompound tileEntityNBT;

    public static float maxRotFac = 60F;

    public EntityBlock(World world)
    {
        super(world);

        rotationRoll = 0.0F;

        rotFacYaw = rand.nextFloat() * (2F * maxRotFac) - maxRotFac;
        rotFacPitch = rand.nextFloat() * (2F * maxRotFac) - maxRotFac;
        rotFacRoll = rand.nextFloat() * (2F * maxRotFac) - maxRotFac;

        setSize(0.95F, 0.95F);

        yOffset = height / 2F;

        preventEntitySpawning = true;
        renderDistanceWeight = 20D;
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

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(16, Block.getIdFromBlock(Blocks.bedrock));//blockID
        dataWatcher.addObject(17, 0);//metadata
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

    @Override
    public void onUpdate()
    {
        if(!worldObj.isRemote && (block == null || block.equals(Blocks.bedrock)))
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

        tileEntityNBT = tag.getCompoundTag("tileEntity");
        if(tileEntityNBT.equals(new NBTTagCompound()))
        {
            tileEntityNBT = null;
            System.out.println("no tile! This check is working correctly! :D");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
        tag.setInteger("block", Block.getIdFromBlock(getBlock()));
        tag.setInteger("meta", getMeta());

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

    //TODO rethink this
    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderOnFire()
    {
        return false;
    }
}
