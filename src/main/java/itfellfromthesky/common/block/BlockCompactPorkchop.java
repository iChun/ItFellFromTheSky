package itfellfromthesky.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import itfellfromthesky.common.ItFellFromTheSky;
import itfellfromthesky.common.core.ChunkLoadHandler;
import itfellfromthesky.common.entity.EntityMeteorite;
import itfellfromthesky.common.entity.EntityPigzilla;
import itfellfromthesky.common.network.ChannelHandler;
import itfellfromthesky.common.network.PacketMeteorSpawn;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class BlockCompactPorkchop extends Block
{
    public BlockCompactPorkchop()
    {
        super(Material.cake);
        this.stepSound = new SoundType("cloth", 1.0F, 1.0F)
        {
            public Random rand = new Random();

            @Override
            public float getPitch()
            {
                return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
            }

            @Override
            public String getBreakSound()
            {
                return "mob.pig.say";
            }

            @Override
            public String getStepResourcePath()
            {
                return "mob.pig.say";
            }

            @Override
            public String func_150496_b()
            {
                return "mob.pig.say";
            }
        };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon("itfellfromthesky:pigblock");
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float hitVecX, float hitVecY, float hitVecZ)
    {
        if(!world.isRemote)
        {
            ItemStack is = player.getHeldItem();
            if(is != null && is.getItem() == Items.nether_star)
            {
                double ranX = world.rand.nextDouble() * 2D - 1D;
                double ranZ = world.rand.nextDouble() * 2D - 1D;

                float f2 = MathHelper.sqrt_double(ranX * ranX + ranZ * ranZ);
                if(f2 < 1F)
                {
                    ranX /= (double)f2;
                    ranZ /= (double)f2;
                }

                double ranY = -world.rand.nextDouble();

                f2 = MathHelper.sqrt_double(ranY * ranY);
                if(f2 < 0.1F)
                {
                    ranY /= (double)f2;
                    ranY *= 0.1F;
                }

                double offsetX = (50D * ranX / ranY);
                double offsetY = 45D;
                double offsetZ = (50D * ranZ / ranY);

                double dist = Math.sqrt(offsetX * offsetX + offsetZ + offsetZ);

                offsetX /= dist;
                offsetY /= dist;
                offsetZ /= dist;
                offsetX *= 150D;
                offsetY *= 150D;
                offsetZ *= 150D;

//                EntityMeteorite meteorite = new EntityMeteorite(world, i + 0.5D +50D, j + 0.5D + 50D, k + 0.5D + 50D);
                EntityMeteorite meteorite = new EntityMeteorite(world, i + 0.5D + offsetX, j + 0.5D + offsetY, k + 0.5D + offsetZ);
//                EntityMeteorite meteorite = new EntityMeteorite(world, i + 0.5D - (ranX * 200D / -(ranY * 2)), j + 0.5D + (200D * -(ranY * 2)), k + 0.5D - (ranZ * 200D / -(ranY * 2)));

                meteorite.motionX = ranX;
                meteorite.motionZ = ranZ;

                meteorite.motionY = ranY;

                meteorite.forceSpawn = true;

                ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(ItFellFromTheSky.instance, world, ForgeChunkManager.Type.ENTITY);
                if(ticket != null)
                {
                    ticket.bindEntity(meteorite);
                    ChunkLoadHandler.addTicket(meteorite, ticket);
                    ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(MathHelper.floor_double(meteorite.posX) >> 4, MathHelper.floor_double(meteorite.posZ) >> 4));
                }

                world.spawnEntityInWorld(meteorite);

                ChannelHandler.sendToDimension(new PacketMeteorSpawn(meteorite.getEntityId(), meteorite.posX, meteorite.posY, meteorite.posZ, meteorite.motionX, meteorite.motionY, meteorite.motionZ, meteorite.rotYaw, meteorite.rotPitch), player.dimension);

                if(!player.capabilities.isCreativeMode)
                {
                    is.stackSize--;
                    if(is.stackSize <= 0)
                    {
                        player.setCurrentItemOrArmor(0, null);
                    }

                    world.playAuxSFX(2001, i, j, k, Block.getIdFromBlock(ItFellFromTheSky.blockCompactPorkchop));
                    world.setBlockToAir(i, j, k);
                }
            }
            else
            {
                ArrayList<EntityPigzilla> list = new ArrayList<EntityPigzilla>();
                for(Map.Entry<Entity, ForgeChunkManager.Ticket> e : ChunkLoadHandler.tickets.entrySet())
                {
                    if(e.getKey() instanceof EntityPigzilla)
                    {
                        list.add((EntityPigzilla)e.getKey());
                    }
                }

                double dist = -1D;
                EntityPigzilla pig = null;

                for(EntityPigzilla pigg : list)
                {
                    if(dist == -1)
                    {
                        pig = pigg;
                        dist = pigg.getDistanceToEntity(player);
                        continue;
                    }
                    if(pigg.getDistanceToEntity(player) < dist)
                    {
                        pig = pigg;
                        dist = pigg.getDistanceToEntity(player);
                    }
                }

                IChatComponent chat = new ChatComponentTranslation("itfellfromthesky.hailPigzilla.hail").setChatStyle((new ChatStyle()).setItalic(true).setColor(EnumChatFormatting.GRAY)).appendSibling(new ChatComponentTranslation("itfellfromthesky.hailPigzilla.hydra").setChatStyle((new ChatStyle()).setStrikethrough(true))).appendSibling(new ChatComponentTranslation("itfellfromthesky.hailPigzilla.pigzilla"));

                if(pig != null)
                {
                    chat.appendText(" ");
                    chat.appendSibling(new ChatComponentTranslation("itfellfromthesky.hailPigzilla.location"));
                    chat.appendText(Integer.toString((int)Math.round(pig.posX)));
                    chat.appendText(", ");
                    chat.appendText(Integer.toString((int)Math.round(pig.posY)));
                    chat.appendText(", ");
                    chat.appendText(Integer.toString((int)Math.round(pig.posZ)));
                }

                player.addChatMessage(chat);
            }
        }
        return true;
    }
}
