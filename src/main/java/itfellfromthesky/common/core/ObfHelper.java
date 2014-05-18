package itfellfromthesky.common.core;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.lang.reflect.Field;

public class ObfHelper
{
    public static boolean obfuscation;

    public static final String[] blockHardness      = new String[] { "field_149782_v", "blockHardness" }; //Block

    public static void detectObfuscation()
    {
        obfuscation = true;
        try
        {
            Field[] fields = World.class.getDeclaredFields();
            for(Field f : fields)
            {
                f.setAccessible(true);
                if(f.getName().equalsIgnoreCase("loadedEntityList"))
                {
                    obfuscation = false;
                    return;
                }
            }
        }
        catch(Exception e){}
    }
}
