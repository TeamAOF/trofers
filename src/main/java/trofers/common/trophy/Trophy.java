package trofers.common.trophy;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class Trophy {

    private final ResourceLocation id;
    private final ItemStack item;
    @Nullable
    private Entity entity;
    @Nullable
    private final EntityType<?> entityType;
    private final CompoundTag entityTag;
    private final boolean animateEntity;
    private final TrophyAnimation animation;
    @Nullable
    private final Component name;
    private final double animationSpeed;
    private final float displayScale;
    private final float displayHeight;
    private final int color;
    private final int accentColor;

    public Trophy(
            ResourceLocation id,
            ItemStack item,
            @Nullable EntityType<?> entityType,
            CompoundTag entityTag,
            boolean animateEntity,
            TrophyAnimation animation,
            @Nullable Component name,
            double animationSpeed,
            float displayScale,
            float displayHeight,
            int color,
            int accentColor
    ) {
        this.id = id;
        this.item = item;
        this.entityType = entityType;
        this.entityTag = entityTag;
        this.animateEntity = animateEntity;
        this.animation = animation;
        this.name = name;
        this.animationSpeed = animationSpeed;
        this.displayScale = displayScale;
        this.displayHeight = displayHeight;
        this.color = color;
        this.accentColor = accentColor;
    }

    public ResourceLocation getId() {
        return id;
    }

    public double getAnimationSpeed() {
        return animationSpeed;
    }

    public double getDisplayScale() {
        return displayScale;
    }

    public double getDisplayHeight() {
        return displayHeight;
    }

    public int getColor() {
        return color;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public TrophyAnimation getAnimation() {
        return animation;
    }

    @Nullable
    public Component getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    @Nullable
    public EntityType<?> getEntityType() {
        return entityType;
    }

    public CompoundTag getEntityTag() {
        return entityTag;
    }

    public boolean shouldAnimateEntity() {
        return animateEntity;
    }

    public boolean hasEntity() {
        return entityType != null;
    }

    @Nullable
    public Entity getOrCreateEntity(Level level) {
        if (entity == null || entity.level != level) {
            createEntity(level);
        }
        return entity;
    }

    private void createEntity(Level level) {
        if (entityType == null) {
            return;
        }

        entity = entityType.create(level);
        if (entity != null) {
            entity.load(entityTag);
            if (!entityTag.hasUUID("UUID")) {
                entity.setUUID(Util.NIL_UUID);
            }
        }
    }

    @Nullable
    public static Trophy getTrophy(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return null;
        }

        CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");

        if (!blockEntityTag.contains("Trophy", Constants.NBT.TAG_STRING)) {
            return null;
        }

        try {
            return TrophyManager.get(new ResourceLocation(blockEntityTag.getString("Trophy")));
        } catch (ResourceLocationException ignored) { }

        return null;
    }

    public static Trophy fromJson(JsonElement element, ResourceLocation id) {
        JsonObject object = GsonHelper.convertToJsonObject(element, "trophy");

        EntityType<?> entityType = null;
        CompoundTag entityTag = new CompoundTag();
        boolean animateEntity = false;
        if (object.has("entity")) {
            JsonObject entity = GsonHelper.getAsJsonObject(object, "entity");
            ResourceLocation typeID = new ResourceLocation(GsonHelper.getAsString(entity, "type"));
            if (!ForgeRegistries.ENTITIES.containsKey(typeID)) {
                throw new JsonParseException(String.format("Unknown entity type %s", typeID));
            }
            entityType = ForgeRegistries.ENTITIES.getValue(typeID);
            if (entity.has("nbt")) {
                JsonElement nbtElement = entity.get("nbt");
                entityTag = readNBT(nbtElement);
            }
            if (entity.has("animateEntity")) {
                animateEntity = GsonHelper.getAsBoolean(entity, "animateEntity");
            }
        }

        ItemStack item = ItemStack.EMPTY;
        if (object.has("item")) {
            item = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(object, "item"), true);
        }

        TrophyAnimation animation = TrophyAnimation.FIXED;
        if (object.has("animation")) {
            animation = TrophyAnimation.fromJson(object.get("animation"));
        }

        double animationSpeed = readOptionalDouble(object, "animationSpeed", 1);
        float displayScale = (float) readOptionalDouble(object, "displayScale", 1);
        float displayHeight = (float) readOptionalDouble(object, "displayHeight", 0);

        int color, accentColor;
        color = accentColor = 0xFFFFFF;
        if (object.has("colors")) {
            JsonObject colors = GsonHelper.getAsJsonObject(object, "colors");
            if (colors.has("base")) {
                color = accentColor = readColor(colors.get("base"));
            }
            if (colors.has("accent")) {
                accentColor = readColor(colors.get("accent"));
            }
        }

        Component name = null;
        if (object.has("name")) {
            name = Component.Serializer.fromJson(object.get("name"));
        }

        return new Trophy(
                id,
                item,
                entityType,
                entityTag,
                animateEntity,
                animation,
                name,
                animationSpeed,
                displayScale,
                displayHeight,
                color,
                accentColor
        );
    }

    private static CompoundTag readNBT(JsonElement element) {
        try {
            if (element.isJsonObject())
                return TagParser.parseTag(TrophyManager.GSON.toJson(element));
            else {
                return TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));
            }
        } catch (CommandSyntaxException exception) {
            throw new JsonSyntaxException(String.format("Invalid NBT Entry: %s", exception));
        }
    }

    private static int readColor(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            int red = GsonHelper.getAsInt(object, "red");
            int green = GsonHelper.getAsInt(object, "green");
            int blue = GsonHelper.getAsInt(object, "blue");
            return red << 16 | green << 8 | blue;
        } else if (element.isJsonPrimitive()) {
            return element.getAsInt();
        } else {
            throw new JsonParseException(String.format("Expected color to be json object or integer, got %s", element));
        }
    }

    private static double readOptionalDouble(JsonObject object, String memberName, int defaultValue) {
        if (object.has(memberName)) {
            return GsonHelper.getAsDouble(object, memberName);
        }
        return defaultValue;
    }
}
