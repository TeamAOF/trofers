package trofers.common.trophy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import trofers.Trofers;
import trofers.common.network.NetworkHandler;
import trofers.common.network.TrophySyncPacket;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TrophyManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {

    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static Map<ResourceLocation, Trophy> trophies = Map.of();

    public TrophyManager() {
        super(GSON, "trofers");
    }

    public static Trophy get(ResourceLocation id) {
        return trophies.getOrDefault(id, null);
    }

    public static Collection<Trophy> values() {
        return trophies.values();
    }

    public static void setTrophies(Map<ResourceLocation, Trophy> trophies) {
        TrophyManager.trophies = trophies;
        Trofers.LOGGER.info("Loaded {} trophies", trophies.size());
    }

    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, Trophy> trophies = new HashMap<>();

        int amountSkipped = 0;
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement element = entry.getValue();
            try {
                if (element.isJsonObject() && element.getAsJsonObject().has("conditions") && !CraftingHelper.processConditions(GsonHelper.getAsJsonArray(element.getAsJsonObject(), "conditions"))) {
                    amountSkipped++;
                } else {
                    trophies.put(id, Trophy.fromJson(element, id));
                }

            } catch (Exception exception) {
                Trofers.LOGGER.error("Couldn't parse trophy {}", id, exception);
            }
        }

        if (amountSkipped > 0) {
            Trofers.LOGGER.info("Skipping loading {} trophies as their conditions were not met", amountSkipped);
        }

        setTrophies(trophies);
    }

    public static void onDataPackReload(PlayerList playerList, @Nullable ServerPlayer player) {
        if (player != null) {
            sync(player);
        } else {
            playerList.getPlayers().forEach(TrophyManager::sync);
        }
    }

    private static void sync(ServerPlayer player) {
        NetworkHandler.INSTANCE.sendToClient(new TrophySyncPacket(trophies), player);
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Trofers.MODID, "trophy_manager");
    }
}
