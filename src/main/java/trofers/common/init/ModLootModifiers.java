package trofers.common.init;

import com.mojang.serialization.Codec;
import io.github.fabricators_of_create.porting_lib.PortingLibRegistries;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import trofers.Trofers;
import trofers.common.loot.AddEntityTrophy;
import trofers.common.loot.AddTrophy;

@SuppressWarnings("unused")
public class ModLootModifiers {

    public static final LazyRegistrar<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = LazyRegistrar.create(PortingLibRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Trofers.MODID);

    public static final RegistryObject<Codec<AddEntityTrophy>> ADD_ENTITY_TROPHY = LOOT_MODIFIERS.register("add_entity_trophy", AddEntityTrophy.CODEC);
    public static final RegistryObject<Codec<AddTrophy>> ADD_TROPHY = LOOT_MODIFIERS.register("add_trophy", AddTrophy.CODEC);
}
