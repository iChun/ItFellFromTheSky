package itfellfromthesky.common.entity;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import itfellfromthesky.common.core.ObfHelper;
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
    public float rotYaw;
    public float rotPitch;
    public float prevRotYaw;
    public float prevRotPitch;

    public double prevMotionX;
    public double prevMotionY;
    public double prevMotionZ;

    public int timeOnGround;

    public Block block;

    //TODO remember to set this when reading a tile's TE.
    //TODO remember to set new XYZ positions in the tag before spawning.
    public NBTTagCompound tileEntityNBT;

    public static float maxRotFac = 25F;

    public EntityBlock(World world)
    {
        super(world);

//        maxRotFac = 25F;

        setSize(0.95F, 0.95F);

        yOffset = height / 2F;

        preventEntitySpawning = true;
        renderDistanceWeight = 20D;
        motionX = 1D;
        motionY = motionZ = 1D;
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
                    motionY = prevMotionY * -(1D / (bounceFactor));

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

        motionX *= 0.98D;
        motionY *= 0.98D;
        motionZ *= 0.98D;

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
        }
    }

    //TODO Read block getUnlocalizedName()..? Prevents mod issues.
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
