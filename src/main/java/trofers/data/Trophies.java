package trofers.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import trofers.Trofers;
import trofers.common.trophy.Trophy;
import trofers.data.trophies.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Trophies implements DataProvider {

    public final List<Trophy> trophies = new ArrayList<>();
    private final DataGenerator generator;

    public Trophies(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    protected void addTrophies() {
        trophies.addAll(VanillaTrophies.createTrophies());
//        if (FabricLoader.getInstance().isModLoaded("alexsmobs"))
//            trophies.addAll(AlexsMobsTrophies.createTrophies());
//        if (FabricLoader.getInstance().isModLoaded("quark"))
//            trophies.addAll(QuarkTrophies.createTrophies());
//        if (FabricLoader.getInstance().isModLoaded("thermal"))
//            trophies.addAll(ThermalTrophies.createTrophies());
        if (FabricLoader.getInstance().isModLoaded("tinkers_construct"))
            trophies.addAll(TinkersConstructTrophies.createTrophies());
    }

    @Override
    public void run(CachedOutput cache) {
        addTrophies();

        Path outputFolder = generator.getOutputFolder();
        Set<ResourceLocation> resourceLocations = Sets.newHashSet();

        for (Trophy trophy : trophies) {
            // noinspection ConstantConditions
            String modId = Registry.ENTITY_TYPE.getKey(trophy.entity().getType()).getNamespace();
            if (!resourceLocations.add(trophy.id())) {
                throw new IllegalStateException("Duplicate trophy " + trophy.id());
            } else {
                Path path = createPath(outputFolder, trophy);
                JsonObject object;
                if (modId.equals("minecraft")) {
                    object = trophy.toJson();
                } else {
                    object = trophy.toJson(DefaultResourceConditions.allModsLoaded(modId));
                }
                saveTrophy(cache, object, path);
            }
        }
    }

    private static void saveTrophy(CachedOutput cache, JsonObject object, Path path) {
        try {
            DataProvider.saveStable(cache, object, path);
        } catch (IOException exception) {
            Trofers.LOGGER.error("Couldn't save trophy {}", path, exception);
        }
    }

    private static Path createPath(Path path, Trophy trophy) {
        return path.resolve("data/" + trophy.id().getNamespace() + "/trofers/" + trophy.id().getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Trophies";
    }
}
