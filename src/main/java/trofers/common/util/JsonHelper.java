package trofers.common.util;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class JsonHelper {

    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static ItemStack deserializeItem(JsonObject object, String memberName) {
        return CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(object, memberName), true);
    }

    public static JsonObject serializeItem(ItemStack item) {
        JsonObject result = new JsonObject();
        // noinspection ConstantConditions
        result.addProperty("item", Registry.ITEM.getKey(item.getItem()).toString());
        if (item.getCount() != 1) {
            result.addProperty("count", item.getCount());
        }
        if (item.hasTag()) {
            // noinspection ConstantConditions
            result.addProperty("nbt", item.getTag().toString());
        }
        return result;
    }

    public static CompoundTag deserializeNBT(JsonElement element) {
        try {
            if (element.isJsonObject())
                return TagParser.parseTag(GSON.toJson(element));
            else {
                return TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));
            }
        } catch (CommandSyntaxException exception) {
            throw new JsonSyntaxException(String.format("Invalid NBT Entry: %s", exception));
        }
    }

    public static List<Boolean> deserializeConditions(JsonObject object, String memberName) {
        JsonArray conditions = GsonHelper.getAsJsonArray(object, memberName);
        List<Boolean> result = new ArrayList<>();

        for(int x = 0; x < conditions.size(); ++x) {
            if (!conditions.get(x).isJsonObject()) {
                throw new JsonSyntaxException("Conditions must be an array of JsonObjects");
            }

            JsonObject json = conditions.get(x).getAsJsonObject();
            result.add(CraftingHelper.getConditionPredicate(json).test(json));
        }

        return result;
    }

    public static JsonElement serializeConditions(List<ConditionJsonProvider> conditions) {
        JsonArray result = new JsonArray();
        for (ConditionJsonProvider condition : conditions) {
            result.add(condition.toJson());
        }
        return result;
    }
}
