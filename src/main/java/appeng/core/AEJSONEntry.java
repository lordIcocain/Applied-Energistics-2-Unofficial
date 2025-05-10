package appeng.core;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Random;

public class AEJSONEntry {
    public String item;
    public int meta_data = 0;
    public int min_value = 0;
    public int max_value = 1;
    public int weight = 1;
    public int exclusiveGroupID = -1;
    //TODO: Replace the int with Integer to see if not including a parameter will just set it to null
    public AEJSONEntry(String item, int meta_data, int min_value, int max_value, int weight, int exclusiveGroupID)
    {
        this.item = item;
        this.meta_data = meta_data;
        this.min_value = min_value;
        this.max_value = max_value;
        this.weight = weight;
        this.exclusiveGroupID = exclusiveGroupID;
    }
    private ItemStack getItemStack(int amount)
    {
        String[] temp = item.split(":");
        if(!temp[0].equals("ore")) {
            return new ItemStack(GameRegistry.findItem(temp[0], temp[1]), amount, meta_data);
        }
        //TODO: Figure this out
            ArrayList<ItemStack> items = OreDictionary.getOres(temp[1]);
            if(items.size() > 0)
                return items.get(0);
        System.err.println("AE2: NO SUCH ORE DICTIONARY OBJECT FOUND: " + item + " | ERROR: Getting itemStack for meteorite loot");
        return new ItemStack(Items.diamond_hoe);
    }
    public ItemStack getItemStack(Random rand)
    {
        return getItemStack(rand.nextInt(this.max_value-min_value)+min_value);
    }

    public String toString()
    {
        return "Entry for " + item + ". Meta data: " + meta_data + ". Minimum and Maximum Value: " + min_value + ", " + max_value + ". Weight and Exclusive Group ID: " + weight + ", " + exclusiveGroupID + ".";
    }
}
