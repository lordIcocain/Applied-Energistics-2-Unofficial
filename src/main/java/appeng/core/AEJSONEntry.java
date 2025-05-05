package appeng.core;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AEJSONEntry {
    public String item;
    public int meta_data = 0;
    public int min_value = 0;
    public int max_value = 1;
    public int weight = 1;
    public int exclusiveGroupID = -1;

    public AEJSONEntry(String dimensionID, String item, int meta_data, int min_value, int max_value, int weight, int exclusiveGroupID)
    {
        this.item = item;
        this.meta_data = meta_data;
        this.min_value = min_value;
        this.max_value = max_value;
        this.weight = weight;
        this.exclusiveGroupID = exclusiveGroupID;
    }
    public AEJSONEntry(String item, int meta_data, int min_value, int max_value, int weight, int exclusiveGroupID)
    {
        this.item = item;
        this.meta_data = meta_data;
        this.min_value = min_value;
        this.max_value = max_value;
        this.weight = weight;
        this.exclusiveGroupID = exclusiveGroupID;
    }
    private List<ItemStack> getItemStacks(int amount)
    {
        String[] temp = item.split(":");
        if(temp[0] != "ore") {
            return Arrays.asList(new ItemStack(GameRegistry.findItem(temp[0], temp[1]), amount, meta_data));
        }
        return OreDictionary.getOres(temp[1]);
    }
    public List<ItemStack> getItemStacks(Random rand)
    {
        return getItemStacks(rand.nextInt(this.max_value-min_value)+min_value);
    }

    public String toString()
    {
        String[] itemID = item.split(":");
        return "Entry for " + GameRegistry.findItem(itemID[0], itemID[1]).getUnlocalizedName() + ". Meta data: " + meta_data + ". Minimum and Maximum Value: " + min_value + ", " + max_value + ". Weight and Exclusive Group ID: " + weight + ", " + exclusiveGroupID + ".";
    }
}
