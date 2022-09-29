package trofers.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import trofers.common.init.ModLootPoolEntries;
import trofers.common.util.JsonHelper;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OptionalLootItem extends LootPoolSingletonContainer {

    private final ResourceLocation item;
    private final List<Boolean> loadingConditions;

    private final List<ConditionJsonProvider> loadingConditionsJson;

    OptionalLootItem(ResourceLocation item, List<Boolean> loadingConditions, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.item = item;
        this.loadingConditions = loadingConditions;
        this.loadingConditionsJson = null;
    }

    OptionalLootItem(ResourceLocation item, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions, ConditionJsonProvider... loadingConditions) {
        super(weight, quality, conditions, functions);
        this.item = item;
        this.loadingConditionsJson = List.of(loadingConditions);
        this.loadingConditions = null;
    }

    public LootPoolEntryType getType() {
        return ModLootPoolEntries.OPTIONAL_ITEM.get();
    }

    @Override
    public boolean expand(LootContext context, Consumer<LootPoolEntry> entries) {
        if (!canRun(context) || !testConditions(loadingConditions)) {
            return false;
        }
        entries.accept(new LootPoolSingletonContainer.EntryBase() {
            public void createItemStack(Consumer<ItemStack> items, LootContext context1) {
                items.accept(new ItemStack(Registry.ITEM.get(item)));
            }
        });
        return true;
    }

    public static boolean testConditions(List<Boolean> conditions) {
        for (Boolean condition : conditions) {
            if (!condition) {
                return false;
            }
        }
        return true;
    }

    public void createItemStack(Consumer<ItemStack> pStackConsumer, LootContext pLootContext) {
        pStackConsumer.accept(new ItemStack(Registry.ITEM.get(item)));
    }

    public static LootPoolSingletonContainer.Builder<?> whenLoaded(Item item) {
        return whenLoaded(Registry.ITEM.getKey(item));
    }

    public static LootPoolSingletonContainer.Builder<?> whenLoaded(ResourceLocation item) {
        return optionalLootItem(item, DefaultResourceConditions.allModsLoaded(item.getNamespace()));
    }

    public static LootPoolSingletonContainer.Builder<?> optionalLootItem(ResourceLocation item, ConditionJsonProvider... loadingConditions) {
        return simpleBuilder((weight, quality, conditions, functions) -> new OptionalLootItem(item, weight, quality, conditions, functions, loadingConditions));
    }

    public static class Serializer extends LootPoolSingletonContainer.Serializer<OptionalLootItem> {

        public void serializeCustom(JsonObject object, OptionalLootItem lootItem, JsonSerializationContext context) {
            super.serializeCustom(object, lootItem, context);
            object.add("when", JsonHelper.serializeConditions(lootItem.loadingConditionsJson));
            object.addProperty("name", lootItem.item.toString());
        }

        protected OptionalLootItem deserialize(JsonObject object, JsonDeserializationContext context, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
            ResourceLocation item = new ResourceLocation(GsonHelper.getAsString(object, "name"));
            List<Boolean> loadingConditions = JsonHelper.deserializeConditions(object, "when");
            if (CraftingHelper.processConditions(object, "when") && !Registry.ITEM.containsKey(item)) {
                throw new JsonParseException("Could not find unknown item " + item);
            }
            return new OptionalLootItem(item, loadingConditions, weight, quality, conditions, functions);
        }
    }
}
