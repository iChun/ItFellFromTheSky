package itfellfromthesky.common.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import itfellfromthesky.common.ItFellFromTheSky;
import itfellfromthesky.common.entity.EntityMeteorite;
import itfellfromthesky.common.entity.EntityPigzilla;
import itfellfromthesky.common.network.ChannelHandler;
import itfellfromthesky.common.network.PacketMeteoriteInfo;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.Map;

public class EventHandler
{
    @SubscribeEvent
    public void onEnterChunk(EntityEvent.EnteringChunk event)
    {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer() && (event.entity instanceof EntityMeteorite || event.entity instanceof EntityPigzilla) && !event.entity.isDead)
        {
            ForgeChunkManager.Ticket ticket = ChunkLoadHandler.tickets.get(event.entity);
            if(ticket == null)
            {
                ticket = ForgeChunkManager.requestTicket(ItFellFromTheSky.instance, event.entity.worldObj, ForgeChunkManager.Type.ENTITY);
                if(ticket != null)
                {
                    ticket.bindEntity(event.entity);
                    ChunkLoadHandler.addTicket(event.entity, ticket);
                }
            }
            if(ticket != null)
            {
                if(event.oldChunkX != 0 && event.oldChunkZ != 0)
                {
                    ForgeChunkManager.unforceChunk(ticket, new ChunkCoordIntPair(event.oldChunkX, event.oldChunkZ));
                }
                ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(event.newChunkX, event.newChunkZ));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntitySpawn(EntityJoinWorldEvent event)
    {
        if(event.entity.worldObj.isRemote && event.entity instanceof EntityMeteorite && FMLCommonHandler.instance().getEffectiveSide().isClient() && !event.entity.isDead)
        {
            event.setCanceled(true);
            if(!event.entity.worldObj.weatherEffects.contains(event.entity))
            {
                event.entity.worldObj.weatherEffects.add(event.entity);
            }
        }
    }
}
