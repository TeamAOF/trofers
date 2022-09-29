package trofers.common.init;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import trofers.Trofers;
import trofers.common.loot.RandomTrophyChanceCondition;

public class ModLootConditions {

    public static final LazyRegistrar<LootItemConditionType> LOOT_CONDITION_TYPES = LazyRegistrar.create(Registry.LOOT_ITEM_REGISTRY, Trofers.MODID);

    public static final RegistryObject<LootItemConditionType> RANDOM_TROPHY_CHANCE = LOOT_CONDITION_TYPES.register("random_trophy_chance", () -> new LootItemConditionType(new RandomTrophyChanceCondition.Serializer()));
}
