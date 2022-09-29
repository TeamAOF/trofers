package trofers.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class DataGenerators implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        var existingData = System.getProperty("trofers.data.existingData").split(";");
        ExistingFileHelper helper = new ExistingFileHelper(Arrays.stream(existingData).map(Paths::get).toList(), Collections.emptySet(),
                true, null, null);

        Trophies trophies = new Trophies(generator);
        generator.addProvider(true, trophies);
        generator.addProvider(true, new LootTables(generator));
        generator.addProvider(true, new BlockTags(generator));
        generator.addProvider(true, new LootModifiers(generator, trophies));

        BlockStates blockStates = new BlockStates(generator, helper);
        generator.addProvider(true, blockStates);
        generator.addProvider(true, new ItemModels(generator, blockStates.models().existingFileHelper));
    }
}
