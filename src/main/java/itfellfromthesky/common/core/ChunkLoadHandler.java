package itfellfromthesky.common.core;

import itfellfromthesky.common.entity.EntityMeteorite;
import itfellfromthesky.common.entity.EntityPigzilla;
import itfellfromthesky.common.entity.EntityTransformer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.List;
import java.util.WeakHashMap;

public class ChunkLoadHandler
        implements ForgeChunkManager.LoadingCallback
{
    public static WeakHashMap<Entity, ForgeChunkManager.Ticket> tickets = new WeakHashMap<Entity, ForgeChunkManager.Ticket>();

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
    {
        for(ForgeChunkManager.Ticket ticket : tickets)
        {
            boolean saved = false;
            Entity ent = ticket.getEntity();
            if(ent instanceof EntityMeteorite || ent instanceof EntityTransformer || ent instanceof EntityPigzilla)
            {
                if(!ent.isDead)
                {
                    saved = true;
                    addTicket(ent, ticket);
                }
            }
            if(!saved)
            {
                ForgeChunkManager.releaseTicket(ticket);
            }
        }
    }

    public static void removeTicket(Entity ent)
    {
        ForgeChunkManager.Ticket ticket = tickets.get(ent);
        if(ticket != null)
        {
            ForgeChunkManager.releaseTicket(ticket);
        }
        tickets.remove(ent);
    }

    public static void addTicket(Entity ent, ForgeChunkManager.Ticket ticket)
    {
        if(ent instanceof EntityMeteorite || ent instanceof EntityTransformer || ent instanceof EntityPigzilla)
        {
            if(tickets.get(ent) != null)
            {
                removeTicket(ent);
            }
            tickets.put(ent, ticket);
        }
    }

    public static void passChunkloadTicket(Entity ent, Entity ent1)
    {
        ForgeChunkManager.Ticket ticket = tickets.get(ent);
        if(ticket != null)
        {
            ticket.bindEntity(ent1);
            tickets.remove(ent);
            tickets.put(ent1, ticket);
        }
    }
}
