package trofers.common.trophy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class EntityInfo {

    private final EntityType<?> type;
    private final CompoundTag nbt;
    private final boolean isAnimated;

    @Nullable
    private Entity entity;

    public EntityInfo(EntityType<?> type, CompoundTag nbt, boolean isAnimated) {
        this.type = type;
        this.nbt = nbt;
        this.isAnimated = isAnimated;
    }

    @Nullable
    public EntityType<?> getType() {
        return type;
    }

    public CompoundTag getTag() {
        return nbt;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    @Nullable
    public Entity getOrCreateEntity(Level level) {
        if (entity == null || entity.level != level) {
            createEntity(level);
        }
        return entity;
    }

    private void createEntity(Level level) {
        if (type == null) {
            return;
        }

        entity = type.create(level);
        if (entity != null) {
            entity.setId(0);
            entity.load(nbt);
            if (!nbt.hasUUID("UUID")) {
                entity.setUUID(Util.NIL_UUID);
            }
        }
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        // noinspection ConstantConditions
        buffer.writeResourceLocation(type.getRegistryName());
        buffer.writeNbt(nbt);
        buffer.writeBoolean(isAnimated);
    }

    public static EntityInfo fromNetwork(FriendlyByteBuf buffer) {
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(buffer.readResourceLocation());
        return new EntityInfo(type, buffer.readNbt(), buffer.readBoolean());
    }

    public JsonObject toJson() {
        JsonObject result = new JsonObject();
        // noinspection ConstantConditions
        result.addProperty("type", getType().getRegistryName().toString());
        if (!getTag().isEmpty()) {
            result.addProperty("nbt", getTag().toString());
        }
        if (isAnimated()) {
            result.addProperty("animated", isAnimated());
        }
        return result;
    }

    public static EntityInfo fromJson(JsonObject object) {
        ResourceLocation typeID = new ResourceLocation(GsonHelper.getAsString(object, "type"));
        if (!ForgeRegistries.ENTITIES.containsKey(typeID)) {
            throw new JsonParseException(String.format("Unknown entity type %s", typeID));
        }
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(typeID);
        CompoundTag nbt = new CompoundTag();
        if (object.has("nbt")) {
            JsonElement nbtElement = object.get("nbt");
            nbt = Trophy.readNBT(nbtElement);
        }
        boolean isAnimated = false;
        if (object.has("animated")) {
            isAnimated = GsonHelper.getAsBoolean(object, "animated");
        }
        return new EntityInfo(type, nbt, isAnimated);
    }
}