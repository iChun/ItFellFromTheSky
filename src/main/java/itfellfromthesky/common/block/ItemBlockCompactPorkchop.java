package itfellfromthesky.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import itfellfromthesky.common.ItFellFromTheSky;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;

import java.util.List;

public class ItemBlockCompactPorkchop extends ItemBlock
{
    public ItemBlockCompactPorkchop(Block blk)
    {
        super(blk);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        par3List.add((new ChatComponentTranslation("itfellfromthesky.desc_compactPorkchop1")).getUnformattedTextForChat());
        if(GuiScreen.isShiftKeyDown())
        {
            par3List.add("");
            par3List.add((new ChatComponentTranslation("itfellfromthesky.desc_compactPorkchop2")).getUnformattedTextForChat());
            par3List.add((new ChatComponentTranslation("itfellfromthesky.desc_compactPorkchop3")).getUnformattedTextForChat());
            par3List.add((new ChatComponentTranslation("itfellfromthesky.desc_compactPorkchop4")).getUnformattedTextForChat());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        super.getSubItems(item, tab, list);
        if(tab != null && tab.equals(ItFellFromTheSky.creativeTabPorkchop))
        {
            list.add(new ItemStack(Items.nether_star, 1, 0));
            list.add(new ItemStack(Items.carrot_on_a_stick, 1, 0));
        }
    }

}
