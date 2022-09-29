package trofers.common.config;

import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import trofers.Trofers;

public class ModConfig {

    public static CommonConfig common;

    public static void registerCommon() {
        Pair<CommonConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        common = commonSpecPair.getLeft();
        ModLoadingContext.registerConfig(Trofers.MODID, net.minecraftforge.fml.config.ModConfig.Type.COMMON, commonSpecPair.getRight());
    }
}
