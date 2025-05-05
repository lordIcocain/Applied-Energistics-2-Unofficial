package appeng.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AEJSONConfig{
    public static AEJSONConfig instance;
    //Conbined weight of items in List<AEJSONEntry>[] is that loottable's weight when selecting a loot table for dimension
    @SerializedName("dimension_loot_tables")
    private Map<String, List<List<AEJSONEntry>>> dimension_loot_tables = new HashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public AEJSONConfig() {}

    public void toFile(File file) {
        try {
            FileUtils.writeStringToFile(file, GSON.toJson(instance), Charset.defaultCharset());
        }
        catch (Exception e) {
            System.err.println("AE2: Could not write json config " + file.getAbsolutePath() + " | Error: Could not create JSON");
        }
    }

    public AEJSONConfig fromFile(File file)
    {
        if (!file.exists()) {
            AEJSONConfig defaultConfig = createDefaultConfig();
            AEJSONConfig.instance = defaultConfig;
            defaultConfig.toFile(file);
            return defaultConfig;
        }
        try {
            AEJSONConfig loaded = GSON.fromJson(FileUtils.readFileToString(file, Charset.defaultCharset()), AEJSONConfig.class);
            AEJSONConfig.instance = loaded;
            return loaded;        }
        catch (Exception e) {
            System.err.println("AE2: Could not read json config " + file.getAbsolutePath() + " | Error: Could not pull JSON from file");
        }
        return new AEJSONConfig();
    }

    public static AEJSONConfig createDefaultConfig() {
        AEJSONConfig config = new AEJSONConfig();
        AEJSONEntry calcProcessorPress = new AEJSONEntry("appliedenergistics2:item.ItemMultiMaterial", 13, 0, 1, 1, 1);
        AEJSONEntry engProcessorPress = new AEJSONEntry("appliedenergistics2:item.ItemMultiMaterial", 14, 0, 1, 1, 1);
        AEJSONEntry logicProcessorPress = new AEJSONEntry("appliedenergistics2:item.ItemMultiMaterial", 15, 0, 1, 1, 1);
        AEJSONEntry siliconPress = new AEJSONEntry("appliedenergistics2:item.ItemMultiMaterial", 19, 0, 1, 1, 1);

        AEJSONEntry IronNugget = new AEJSONEntry("ore:nuggetIron", 0, 0, 12, 1, -1);
        AEJSONEntry CopperNugget = new AEJSONEntry("ore:nuggetCopper", 0, 0, 12, 1, 3);
        AEJSONEntry TinNugget = new AEJSONEntry("ore:nuggetTin", 0, 0, 12, 1, 4);
        AEJSONEntry SilverNugget = new AEJSONEntry("ore:nuggetSilver", 0, 0, 12, 1, 5);
        AEJSONEntry LeadNugget = new AEJSONEntry("ore:nuggetLead", 0, 0, 12, 1, 2);
        AEJSONEntry PlatinumNugget = new AEJSONEntry("ore:nuggetPlatinum", 0, 0, 12, 1, 3);
        AEJSONEntry NickelNugget = new AEJSONEntry("ore:nuggetNickel", 0, 0, 12, 1, 4);
        AEJSONEntry AluminiumNugget = new AEJSONEntry("ore:nuggetAluminium", 0, 0, 12, 1, 5);
        AEJSONEntry ElectrumNugget = new AEJSONEntry("ore:nuggetElectrum", 0, 0, 12, 1, 2);
        AEJSONEntry GoldNugget = new AEJSONEntry("minecraft:gold_nugget", 0, 0, 12, 1, -1);
        AEJSONEntry Diamond = new AEJSONEntry("minecraft:diamond", 0, 2, 4, 1, 1);
        config.dimension_loot_tables.put("0", Arrays.asList(
                Arrays.asList(calcProcessorPress, CopperNugget, PlatinumNugget, IronNugget, GoldNugget),
                Arrays.asList(engProcessorPress, TinNugget, NickelNugget, IronNugget, GoldNugget),
                Arrays.asList(logicProcessorPress, SilverNugget, AluminiumNugget, IronNugget, GoldNugget),
                Arrays.asList(siliconPress, LeadNugget, ElectrumNugget, IronNugget, GoldNugget)
                                                            ));
        config.dimension_loot_tables.put("-29", Arrays.asList(
                Arrays.asList(calcProcessorPress, CopperNugget, PlatinumNugget, IronNugget, GoldNugget, Diamond),
                Arrays.asList(engProcessorPress, TinNugget, NickelNugget, IronNugget, GoldNugget, Diamond),
                Arrays.asList(logicProcessorPress, SilverNugget, AluminiumNugget, IronNugget, GoldNugget, Diamond),
                Arrays.asList(siliconPress, LeadNugget, ElectrumNugget, IronNugget, GoldNugget, Diamond)
        ));
        return config;
    }





    private List<List<AEJSONEntry>> getTablesForDimension(int dimensionID)
    {
        if(DimensionManager.isDimensionRegistered(dimensionID)) {
            if(dimension_loot_tables.containsKey(dimensionID+"")) {
                return dimension_loot_tables.get(dimensionID+"");
            }
            else if (dimension_loot_tables.containsKey("0")) {
                return dimension_loot_tables.get("0");
            }
            else {
                return createDefaultConfig().getTablesForDimension(0);
            }
        }
        else {
            System.err.println("AE2: Failure While Getting Loot Tables for Dimension: " + dimensionID + " | Error: Dimension is not registered");
            return dimension_loot_tables.get("0");
        }
    }
    public List<AEJSONEntry> getWeightedLootTable(int dimID, Random rand)
    {
        List<List<AEJSONEntry>> loot_tables = instance.getTablesForDimension(dimID);
        if (loot_tables == null || loot_tables.isEmpty()) {
            System.err.println("AE2: No loot tables found for dimension, will use default loot table" + dimID + " | Error: Empty or missing loot tables.");
            loot_tables =  createDefaultConfig().getTablesForDimension(dimID);
        }
        int[] totalWeights = new int[loot_tables.size()];
        int totalWeight = 0;
        for (int i = 0; i < loot_tables.size(); i++) {
            for (AEJSONEntry entry : loot_tables.get(i)) {
                totalWeights[i] += entry.weight;
                totalWeight += entry.weight;
            }
        }

        int randomWeight = rand.nextInt(totalWeight);
        int cumulitive = 0;
        for(int i = 0; i < totalWeights.length; i++) {
            cumulitive+=totalWeights[i];
            if(randomWeight <= cumulitive) {
                return loot_tables.get(i);
            }
        }
        System.err.println("AE2: Failed to pull a weighted random loot_table for dimension: " + dimID + ", pulling unweighted random loot_table. | Error: Weighted random failed. THIS IS LIKELY A BUG.");
        return loot_tables.get(rand.nextInt(loot_tables.size()));

    }
}
