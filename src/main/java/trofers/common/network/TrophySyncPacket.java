package trofers.common.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import trofers.common.block.entity.TrophyScreen;
import trofers.common.trophy.Trophy;
import trofers.common.trophy.TrophyManager;

import java.util.HashMap;
import java.util.Map;

public class TrophySyncPacket implements S2CPacket {

    private final Map<ResourceLocation, Trophy> trophies;

    @SuppressWarnings("unused")
    public TrophySyncPacket(FriendlyByteBuf buffer) {
        trophies = new HashMap<>();
        while(buffer.readBoolean()) {
            Trophy trophy = Trophy.fromNetwork(buffer);
            trophies.put(trophy.id(), trophy);
        }
    }

    public TrophySyncPacket(Map<ResourceLocation, Trophy> trophies) {
        this.trophies = trophies;
    }

    @SuppressWarnings("unused")
    public void encode(FriendlyByteBuf buffer) {
        trophies.values().forEach(trophy -> {
            buffer.writeBoolean(true);
            trophy.toNetwork(buffer);
        });
        buffer.writeBoolean(false);
    }

    public void handle(Minecraft client, ClientPacketListener listener, PacketSender sender, SimpleChannel simpleChannel) {
        client.execute(() -> {
            TrophyManager.setTrophies(trophies);
            TrophyScreen.SearchTreeManager.createSearchTree();
        });
    }
}
