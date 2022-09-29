package trofers.common.init;

import io.github.fabricators_of_create.porting_lib.util.LazyItemGroup;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import trofers.Trofers;
import trofers.common.block.TrophyBlock;
import trofers.common.item.TrophyItem;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class ModItems {

    public static final LazyRegistrar<Item> ITEMS = LazyRegistrar.create(Registry.ITEM_REGISTRY, Trofers.MODID);

    public static final CreativeModeTab CREATIVE_TAB = new LazyItemGroup(Trofers.MODID) {
        @Override
        public ItemStack makeIcon() {
            ItemStack result = new ItemStack(MEDIUM_PILLAR.get());
            result.getOrCreateTagElement("BlockEntityTag")
                    .putString("Trophy", new ResourceLocation(Trofers.MODID, "panda").toString());
            return result;
        }
    };

    public static final Set<RegistryObject<TrophyItem>> TROPHIES = new HashSet<>();

    public static final RegistryObject<TrophyItem> SMALL_PILLAR = addTrophy(ModBlocks.SMALL_PILLAR);
    public static final RegistryObject<TrophyItem> MEDIUM_PILLAR = addTrophy(ModBlocks.MEDIUM_PILLAR);
    public static final RegistryObject<TrophyItem> LARGE_PILLAR = addTrophy(ModBlocks.LARGE_PILLAR);
    public static final RegistryObject<TrophyItem> SMALL_PLATE = addTrophy(ModBlocks.SMALL_PLATE);
    public static final RegistryObject<TrophyItem> MEDIUM_PLATE = addTrophy(ModBlocks.MEDIUM_PLATE);
    public static final RegistryObject<TrophyItem> LARGE_PLATE = addTrophy(ModBlocks.LARGE_PLATE);

    private static RegistryObject<TrophyItem> addTrophy(RegistryObject<TrophyBlock> block) {
        RegistryObject<TrophyItem> trophy = ITEMS.register(block.getId().getPath(), () ->
                new TrophyItem(
                        block.get(),
                        new Item.Properties()
                                .fireResistant()
                                .tab(CREATIVE_TAB)
                )
        );
        TROPHIES.add(trophy);
        return trophy;
    }
}
