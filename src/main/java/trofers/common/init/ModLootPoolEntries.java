package trofers.common.init;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import trofers.Trofers;
import trofers.common.loot.OptionalLootItem;

public class ModLootPoolEntries {

    public static final LazyRegistrar<LootPoolEntryType> LOOT_POOL_ENTRY_TYPES = LazyRegistrar.create(Registry.LOOT_ENTRY_REGISTRY, Trofers.MODID);

    public static final RegistryObject<LootPoolEntryType> OPTIONAL_ITEM = LOOT_POOL_ENTRY_TYPES.register("optional_item", () -> new LootPoolEntryType(new OptionalLootItem.Serializer()));

}
