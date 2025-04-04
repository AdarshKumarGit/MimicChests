package org.chubby.github.mmimicchests;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Common COMMON = new Common(BUILDER);
    public static final ForgeConfigSpec COMMON_CONFIG = BUILDER.build();

    public static class Common {
        public final ForgeConfigSpec.DoubleValue mimicChestSpawnChance;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            mimicChestSpawnChance = builder
                    .comment("Chance for a chest to spawn as a mimic (0.0 to 1.0)")
                    .defineInRange("mimicChestSpawnChance", 0.1, 0.0, 1.0);

            builder.pop();
        }
    }
}