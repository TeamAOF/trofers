package trofers;

import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import trofers.common.block.entity.TrophyBlockEntity;
import trofers.common.block.entity.TrophyBlockEntityRenderer;
import trofers.common.block.entity.TrophyScreen;
import trofers.common.init.ModBlockEntityTypes;
import trofers.common.init.ModBlocks;
import trofers.common.init.ModItems;
import trofers.common.trophy.Trophy;

public class TrofersClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.onClientSetup();
        this.onBlockColorHandler();
        this.onItemColorHandler();
        this.onRegisterClientReloadListeners();
    }

    public void onClientSetup() {
        BlockEntityRendererRegistry.register(ModBlockEntityTypes.TROPHY.get(), TrophyBlockEntityRenderer::new);
    }

    public void onRegisterClientReloadListeners() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new TrophyScreen.SearchTreeManager());
    }

    public void onBlockColorHandler() {
        ColorProviderRegistry.BLOCK.register((state, level, pos, index) -> {
            if (index >= 0 && index < 3 && level != null && pos != null) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof TrophyBlockEntity trophyBlockEntity) {
                    Trophy trophy = trophyBlockEntity.getTrophy();
                    if (trophy == null) {
                        return 0xFFFFFF;
                    }

                    if (index == 0) {
                        return trophy.colors().base();
                    } else if (index == 1) {
                        return trophy.colors().accent();
                    }
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.TROPHIES.stream().map(RegistryObject::get).toArray(Block[]::new));
    }

    public void onItemColorHandler() {
        ColorProviderRegistry.ITEM.register((stack, index) -> {
            Trophy trophy = Trophy.getTrophy(stack);
            if (trophy != null) {
                if (index == 0) {
                    return trophy.colors().base();
                } else if (index == 1) {
                    return trophy.colors().accent();
                }
            }
            return 0xFFFFFF;
        }, ModItems.TROPHIES.stream().map(RegistryObject::get).toArray(Item[]::new));
    }
}
