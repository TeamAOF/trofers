package trofers.common.network;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.api.EnvType;
import net.minecraft.resources.ResourceLocation;
import trofers.Trofers;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = new SimpleChannel(
            new ResourceLocation(Trofers.MODID, "main")
    );

    public static void register() {
        INSTANCE.registerC2SPacket(SetTrophyPacket.class, 0);
        INSTANCE.registerS2CPacket(TrophySyncPacket.class, 1);

        INSTANCE.initServerListener();

        EnvExecutor.runWhenOn(EnvType.CLIENT, () -> INSTANCE::initClientListener);
    }
}
