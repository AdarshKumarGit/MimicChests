package org.chubby.github.mmimicchests.registry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.chubby.github.mmimicchests.Constants;
import org.chubby.github.mmimicchests.entity.ChestEntity;
import org.chubby.github.mmimicchests.entity.ThrownItemEntity;

@SuppressWarnings("unchecked")
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);

    public static final RegistryObject<EntityType<ChestEntity>> MIMIC_CHEST = ENTITIES.register("mimic_chest",
            () -> EntityType.Builder.<ChestEntity>of(ChestEntity::new, MobCategory.MISC).sized(1.0f,1.0f).build("mimic_chest"));

    public static final RegistryObject<EntityType<ThrownItemEntity>> THROWN_ITEM = ENTITIES.register("thrown_item",
            () -> EntityType.Builder.<ThrownItemEntity>of(
                            (entityType, level) -> new ThrownItemEntity((EntityType<ThrownItemEntity>)entityType, level, ItemStack.EMPTY),
                            MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .updateInterval(20)
                    .clientTrackingRange(16)
                    .setTrackingRange(256)
                    .noSummon()
                    .build("thrown_item"));
}
