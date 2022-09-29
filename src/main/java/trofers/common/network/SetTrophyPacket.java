package trofers.common.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import trofers.common.block.entity.TrophyBlockEntity;
import trofers.common.trophy.Trophy;
import trofers.common.trophy.TrophyManager;

public class SetTrophyPacket implements C2SPacket {

    private final Trophy trophy;
    private final BlockPos blockPos;

    @SuppressWarnings("unused")
    public SetTrophyPacket(FriendlyByteBuf buffer) {
        this.trophy = TrophyManager.get(buffer.readResourceLocation());
        this.blockPos = buffer.readBlockPos();
    }

    public SetTrophyPacket(Trophy trophy, BlockPos blockPos) {
        this.trophy = trophy;
        this.blockPos = blockPos;
    }

    @SuppressWarnings("unused")
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(trophy.id());
        buffer.writeBlockPos(blockPos);
    }

    public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, PacketSender sender, SimpleChannel simpleChannel) {
        if (player != null) {
            server.execute(() -> {
                if (player.isCreative()
                        && player.level.isLoaded(blockPos)
                        && player.level.getBlockEntity(blockPos) instanceof TrophyBlockEntity blockEntity
                ) {
                    blockEntity.setTrophy(trophy);
                }
            });
        }
    }
}
