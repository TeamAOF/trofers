package trofers;

import io.github.fabricators_of_create.porting_lib.event.common.OnDatapackSyncCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import trofers.common.config.ModConfig;
import trofers.common.init.*;
import trofers.common.network.NetworkHandler;
import trofers.common.trophy.TrophyManager;

public class Trofers implements ModInitializer {

    public static final String MODID = "trofers";

    public static final Logger LOGGER = LogManager.getLogger("Trofers");

    @Override
    public void onInitialize() {
        ModConfig.registerCommon();

        ModItems.ITEMS.register();
        ModBlocks.BLOCKS.register();
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register();
        ModLootModifiers.LOOT_MODIFIERS.register();
        ModLootConditions.LOOT_CONDITION_TYPES.register();
        ModLootPoolEntries.LOOT_POOL_ENTRY_TYPES.register();

        this.onCommonSetup();

        this.onAddReloadListener();
        OnDatapackSyncCallback.EVENT.register(TrophyManager::onDataPackReload);
    }

    public void onCommonSetup() {
        NetworkHandler.register();
    }

    public void onAddReloadListener() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new TrophyManager());
    }
}
