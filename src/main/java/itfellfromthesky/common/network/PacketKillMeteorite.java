package itfellfromthesky.common.network;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import itfellfromthesky.common.entity.EntityMeteorite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PacketKillMeteorite extends IPacket
{

    public int entId;

    public PacketKillMeteorite(){}

    public PacketKillMeteorite(int id)
    {
        entId = id;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(entId);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side, EntityPlayer player)
    {
        entId = buffer.readInt();

        for(int i = player.worldObj.weatherEffects.size() - 1; i >= 0; i--)
        {
            Entity ent = (Entity)player.worldObj.weatherEffects.get(i);
            if(ent instanceof EntityMeteorite)
            {
                if(ent.getEntityId() == entId)
                {
                    ((EntityMeteorite)ent).canSetDead = true;
                    ent.setDead();
                }
            }
        }
    }
}
