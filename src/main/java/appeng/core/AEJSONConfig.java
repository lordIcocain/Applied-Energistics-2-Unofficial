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
        AEJSONEntry calcProcessorPress;
        AEJSONEntry engProcessorPress ;
        AEJSONEntry logicProcessorPress;
        AEJSONEntry siliconPress;
        AEJSONEntry goldNugget = new AEJSONEntry("minecraft:gold_nugget", 0, 0, 12, 1, -1);

        config.dimension_loot_tables.put("0", Arrays.asList(Arrays.asList(goldNugget)));
        return config;
    }





    public List<List<AEJSONEntry>> getTablesForDimension(int dimensionID)
    {
        if(DimensionManager.isDimensionRegistered(dimensionID)) {
            if(dimension_loot_tables.containsKey(dimensionID+"")) {
                return dimension_loot_tables.get(dimensionID+"");
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
