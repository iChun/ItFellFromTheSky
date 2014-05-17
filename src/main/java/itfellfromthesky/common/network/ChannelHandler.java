package itfellfromthesky.common.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import itfellfromthesky.common.ItFellFromTheSky;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

import java.util.EnumMap;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<IPacket>
{
    public ChannelHandler(Class<? extends IPacket>...packets)
    {
        for(int i = 0; i < packets.length; i++)
        {
            addDiscriminator(i, packets[i]);
        }
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, IPacket msg, ByteBuf target) throws Exception
    {
        try
        {
            msg.writeTo(target, FMLCommonHandler.instance().getEffectiveSide());
        }
        catch(Exception e)
        {
            ItFellFromTheSky.console("Error writing to packet!", true);
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, IPacket msg)
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        EntityPlayer player = null;
        if(side.isServer())
        {
            INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            player = ((NetHandlerPlayServer)netHandler).playerEntity;
        }
        else
        {
            player = this.getMCPlayer();
        }
        try
        {
            msg.readFrom(source, side, player);
        }
        catch(Exception e)
        {
            ItFellFromTheSky.console("Error reading from packet!", true);
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    public EntityPlayer getMCPlayer()
    {
        return Minecraft.getMinecraft().thePlayer;
    }

    public static void sendToDimension(IPacket packet, int dimension)
    {
        ItFellFromTheSky.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        ItFellFromTheSky.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
        ItFellFromTheSky.channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public static void sendToServer(IPacket packet)
    {
        ItFellFromTheSky.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        ItFellFromTheSky.channels.get(Side.CLIENT).writeAndFlush(packet);
    }

}
