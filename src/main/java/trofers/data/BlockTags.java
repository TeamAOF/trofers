package trofers.data;

import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.world.level.block.Block;
import trofers.common.init.ModBlocks;

public class BlockTags extends FabricTagProvider.BlockTagProvider {

    public BlockTags(FabricDataGenerator generator) {
        super(generator);
    }

    @Override
    protected void generateTags() {
        tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.TROPHIES.stream().map(RegistryObject::get).toArray(Block[]::new)
        );
    }
}
