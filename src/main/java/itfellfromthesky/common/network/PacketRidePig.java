package itfellfromthesky.common.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import itfellfromthesky.common.entity.EntityPigzilla;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PacketRidePig extends IPacket
{
    public int entId;
    public String playerName;

    public PacketRidePig(){}

    public PacketRidePig(EntityPigzilla pig, EntityPlayer player)
    {
        entId = pig.getEntityId();
        playerName = player.getCommandSenderName();
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(entId);
        ByteBufUtils.writeUTF8String(buffer, playerName);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side, EntityPlayer player)
    {
        entId = buffer.readInt();
        playerName = ByteBufUtils.readUTF8String(buffer);

        EntityPlayer player1 = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(playerName);
        if(player1.equals(player))
        {
            Entity ent = player.worldObj.getEntityByID(entId);
            if(ent instanceof EntityPigzilla && ent.riddenByEntity == null)
            {
                player.mountEntity(ent);
            }
        }
    }
}
