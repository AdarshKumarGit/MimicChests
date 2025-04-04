package org.chubby.github.mmimicchests;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.chubby.github.mmimicchests.client.model.ChestEntityModel;
import org.chubby.github.mmimicchests.client.renderer.ChestEntityRenderer;
import org.chubby.github.mmimicchests.client.screens.ModConfigScreen;
import org.chubby.github.mmimicchests.entity.ChestEntity;
import org.chubby.github.mmimicchests.registry.ModEntities;

@Mod(Constants.MOD_ID)
public class Mmimicchests {

    public Mmimicchests() {
        FMLJavaModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,Config.COMMON_CONFIG);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(ModConfigScreen::new)
            );
        });
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
        @SubscribeEvent
        public static void buildEntityAttributes(EntityAttributeCreationEvent event){
            event.put(ModEntities.MIMIC_CHEST.get(), ChestEntity.createAts().build());
        }
        @SubscribeEvent
        public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event){
            event.registerLayerDefinition(ChestEntityModel.LOCATION,ChestEntityModel::createBodyLayer);
        }
        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
            event.registerEntityRenderer(ModEntities.MIMIC_CHEST.get(), ChestEntityRenderer::new);
        }
    }
}
