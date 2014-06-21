package itfellfromthesky.common.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import itfellfromthesky.common.entity.EntityMeteorite;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PacketMeteoriteInfo extends AbstractPacket
{
    public int entId;

    public double moX;
    public double moY;
    public double moZ;

    public float rY;
    public float rP;

    public double pX;
    public double pY;
    public double pZ;

    public float rotYaw;
    public float rotPitch;

    public PacketMeteoriteInfo(){}

    public PacketMeteoriteInfo(EntityMeteorite meteorite)
    {
        entId = meteorite.getEntityId();
        moX = meteorite.motionX;
        moY = meteorite.motionY;
        moZ = meteorite.motionZ;

        rY = meteorite.getRotFacYaw();
        rP = meteorite.getRotFacPitch();

        pX = meteorite.posX;
        pY = meteorite.posY;
        pZ = meteorite.posZ;

        rotYaw = meteorite.rotYaw;
        rotPitch = meteorite.rotPitch;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(entId);
        buffer.writeDouble(moX);
        buffer.writeDouble(moY);
        buffer.writeDouble(moZ);

        buffer.writeFloat(rY);
        buffer.writeFloat(rP);

        buffer.writeDouble(pX);
        buffer.writeDouble(pY);
        buffer.writeDouble(pZ);

        buffer.writeFloat(rotYaw);
        buffer.writeFloat(rotPitch);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        entId = buffer.readInt();

        moX = buffer.readDouble();
        moY = buffer.readDouble();
        moZ = buffer.readDouble();

        rY = buffer.readFloat();
        rP = buffer.readFloat();

        pX = buffer.readDouble();
        pY = buffer.readDouble();
        pZ = buffer.readDouble();

        rotYaw = buffer.readFloat();
        rotPitch = buffer.readFloat();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        if(side.isClient())
        {
            handleClient();
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        for(int i = 0; i < Minecraft.getMinecraft().theWorld.weatherEffects.size(); i++)
        {
            Entity ent = (Entity)Minecraft.getMinecraft().theWorld.weatherEffects.get(i);
            if(ent instanceof EntityMeteorite && ((EntityMeteorite)ent).getEntityId() == entId)
            {
                EntityMeteorite meteorite = ((EntityMeteorite)ent);
                meteorite.updateMotion(moX, moY, moZ);
                meteorite.setRotFacYaw(rY);
                meteorite.setRotFacPitch(rP);

                if(moX == 0.0D && moY == 0.0D && moZ == 0.0D)
                {
                    meteorite.setPosition(pX, pY, pZ);
                    meteorite.stopped = true;
                }

                if(meteorite.ticksExisted < 5)
                {
                    meteorite.rotYaw = rotYaw;
                    meteorite.rotPitch = rotPitch;
                }
                break;
            }
        }
    }
}
