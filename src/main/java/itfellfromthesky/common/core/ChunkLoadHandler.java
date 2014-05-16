package itfellfromthesky.common.core;

import itfellfromthesky.common.entity.EntityMeteorite;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

public class ChunkLoadHandler
    implements ForgeChunkManager.LoadingCallback
{
    public static WeakHashMap<EntityMeteorite, ForgeChunkManager.Ticket> tickets = new WeakHashMap<EntityMeteorite, ForgeChunkManager.Ticket>();

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
    {
        for(ForgeChunkManager.Ticket ticket : tickets)
        {
            boolean saved = false;
            if(ticket.getEntity() instanceof  EntityMeteorite)
            {
                EntityMeteorite meteorite = (EntityMeteorite)ticket.getEntity();
                if(!meteorite.isDead)
                {
                    saved = true;
                    addTicket(meteorite, ticket);
                }
            }
            if(!saved)
            {
                ForgeChunkManager.releaseTicket(ticket);
            }
        }
    }

    public static void removeTicket(EntityMeteorite ent)
    {
        ForgeChunkManager.Ticket ticket = tickets.get(ent);
        if(ticket != null)
        {
            ForgeChunkManager.releaseTicket(ticket);
        }
        tickets.remove(ent);
    }

    public static void addTicket(EntityMeteorite ent, ForgeChunkManager.Ticket ticket)
    {
        if(ent != null)
        {
            if(tickets.get(ent) != null)
            {
                removeTicket(ent);
            }
            tickets.put(ent, ticket);
        }
    }
}
