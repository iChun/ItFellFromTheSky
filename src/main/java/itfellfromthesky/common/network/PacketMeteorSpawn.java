package itfellfromthesky.common.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import itfellfromthesky.common.entity.EntityMeteorite;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PacketMeteorSpawn extends AbstractPacket
{
    public int entId;
    public double x;
    public double y;
    public double z;
    public double mX;
    public double mY;
    public double mZ;
    public float rY;
    public float rP;

    public PacketMeteorSpawn(){}

    public PacketMeteorSpawn(int id, double X, double Y, double Z, double MX, double MY, double MZ, float RY, float RP)
    {
        entId = id;
        x = X;
        y = Y;
        z = Z;
        mX = MX;
        mY = MY;
        mZ = MZ;
        rY = RY;
        rP = RP;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(entId);
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
        buffer.writeDouble(mX);
        buffer.writeDouble(mY);
        buffer.writeDouble(mZ);
        buffer.writeFloat(rY);
        buffer.writeFloat(rP);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        if(side.isClient())
        {
            entId = buffer.readInt();
            x = buffer.readDouble();
            y = buffer.readDouble();
            z = buffer.readDouble();
            mX = buffer.readDouble();
            mY = buffer.readDouble();
            mZ = buffer.readDouble();
            rY = buffer.readFloat();
            rP = buffer.readFloat();

            handleClient();
        }
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {

    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        EntityMeteorite meteorite = new EntityMeteorite(Minecraft.getMinecraft().theWorld);
        meteorite.setEntityId(entId);

        meteorite.setLocationAndAngles(x, y, z, rY, rP);
        meteorite.setVelocity(mX, mY, mZ);
        meteorite.rotYaw = rY;
        meteorite.rotPitch = rP;

        boolean exists = false;
        for(int i = 0; i < Minecraft.getMinecraft().theWorld.weatherEffects.size(); i++)
        {
            Entity ent = (Entity)Minecraft.getMinecraft().theWorld.weatherEffects.get(i);
            if(ent instanceof EntityMeteorite && ((EntityMeteorite)ent).getEntityId() == entId)
            {
                exists = true;
                break;
            }
        }
        if(!exists)
        {
            Minecraft.getMinecraft().theWorld.weatherEffects.add(meteorite);
        }
    }
}
