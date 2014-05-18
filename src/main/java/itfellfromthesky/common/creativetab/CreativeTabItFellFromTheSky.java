package itfellfromthesky.common.creativetab;

import itfellfromthesky.common.ItFellFromTheSky;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabItFellFromTheSky extends CreativeTabs
{
    public CreativeTabItFellFromTheSky()
    {
        super("itfellfromthesky");
    }

    @Override
    public Item getTabIconItem()
    {
        return Item.getItemFromBlock(ItFellFromTheSky.blockCompactPorkchop);
    }
}
