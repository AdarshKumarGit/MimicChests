package org.chubby.github.mmimicchests.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.chubby.github.mmimicchests.registry.ModEntities;
import org.jetbrains.annotations.NotNull;

public class ThrownItemEntity extends ThrowableItemProjectile {

    public final ItemStack baseItem;

    public ThrownItemEntity(EntityType<ThrownItemEntity> pEntity,Level p_37443_, ItemStack baseItem) {
        super(pEntity, p_37443_);
        this.baseItem = baseItem;
    }

    public ThrownItemEntity(double p_37433_, double p_37434_, double p_37435_, Level p_37436_, ItemStack baseItem) {
        super(ModEntities.THROWN_ITEM.get(), p_37433_, p_37434_, p_37435_, p_37436_);
        this.baseItem = baseItem;
    }

    public ThrownItemEntity(LivingEntity p_37439_, Level p_37440_, ItemStack baseItem) {
        super(ModEntities.THROWN_ITEM.get(), p_37439_, p_37440_);
        this.baseItem = baseItem;
    }


    @Override
    protected @NotNull Item getDefaultItem() {
        return baseItem.getItem();
    }
}
