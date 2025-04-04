package org.chubby.github.mmimicchests.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class AbstractChestEntity extends Monster {

    public static final EntityDataAccessor<Boolean> DATA_OPENED_ID =
            SynchedEntityData.defineId(AbstractChestEntity.class, EntityDataSerializers.BOOLEAN);

    private UUID targetPlayerUUID = null;
    private final List<ItemStack> storedItems = new ArrayList<>();
    private final Random random = new Random();
    private int attackCooldown = 0;

    public AbstractChestEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_OPENED_ID, false);
        super.defineSynchedData();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(DATA_OPENED_ID, compoundTag.getBoolean("Opened"));

        if (compoundTag.hasUUID("TargetPlayerUUID")) {
            this.targetPlayerUUID = compoundTag.getUUID("TargetPlayerUUID");
        }

        if (compoundTag.contains("Items", 9)) {
            ListTag itemsList = compoundTag.getList("Items", 10);
            for (int i = 0; i < itemsList.size(); i++) {
                CompoundTag itemTag = itemsList.getCompound(i);
                ItemStack itemStack = ItemStack.of(itemTag);
                if (!itemStack.isEmpty()) {
                    storedItems.add(itemStack);
                }
            }
            updateHealthBasedOnItems();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Opened", this.entityData.get(DATA_OPENED_ID));

        if (this.targetPlayerUUID != null) {
            compoundTag.putUUID("TargetPlayerUUID", this.targetPlayerUUID);
        }

        ListTag itemsList = new ListTag();
        for (int i = 0; i < storedItems.size(); i++) {
            ItemStack itemStack = storedItems.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemStack.save(itemTag);
                itemsList.add(itemTag);
            }
        }
        compoundTag.put("Items", itemsList);
    }

    public void setStoredItems(List<ItemStack> items) {
        this.storedItems.clear();
        this.storedItems.addAll(items);
        updateHealthBasedOnItems();
    }

    private void updateHealthBasedOnItems() {
        int totalStacks = storedItems.size();
        int totalItems = storedItems.stream().mapToInt(ItemStack::getCount).sum();

        float baseHealth = 16.0F;
        float additionalHealth = totalItems * 0.1F + totalStacks * 0.5F;

        this.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(baseHealth + additionalHealth);
        this.setHealth(this.getMaxHealth());
    }

    public boolean isOpened() {
        return this.entityData.get(DATA_OPENED_ID);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (!isOpened()) {
            return false;
        }

        if (target instanceof Player player && targetPlayerUUID != null) {
            return player.getUUID().equals(targetPlayerUUID) && super.canAttack(target);
        }

        return false;
    }

    @Override
    public boolean canAttack(LivingEntity target, TargetingConditions conditions) {
        if (!isOpened()) {
            return false;
        }

        if (target instanceof Player player && targetPlayerUUID != null) {
            return player.getUUID().equals(targetPlayerUUID) && super.canAttack(target, conditions);
        }

        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return isOpened() && super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (source.getEntity() instanceof Player player) {
            onKilledByPlayer(player);
            dropStoredItems();
        }
        super.die(source);
    }

    private void dropStoredItems() {
        if (!level().isClientSide() && !storedItems.isEmpty()) {
            for (ItemStack item : storedItems) {
                this.spawnAtLocation(item);
            }
            storedItems.clear();
        }
    }

    public void shootStoredItem() {
        if (!storedItems.isEmpty() && !level().isClientSide() && this.getTarget() != null) {
            int index = random.nextInt(storedItems.size());
            ItemStack itemToShoot = storedItems.get(index).copy();
            itemToShoot.setCount(1);

            if (storedItems.get(index).getCount() > 1) {
                storedItems.get(index).shrink(1);
            } else {
                storedItems.remove(index);
            }

            Vec3 targetPos = this.getTarget().position().add(0, this.getTarget().getEyeHeight() / 2, 0);
            Vec3 direction = targetPos.subtract(this.position().add(0, this.getEyeHeight(), 0)).normalize();

            ThrownItemEntity thrownItem = new ThrownItemEntity(this.getX(), this.getY() + this.getEyeHeight(), this.getZ(),level(), itemToShoot);
            thrownItem.setDeltaMovement(direction.x * 1.5, direction.y * 1.5, direction.z * 1.5);
            thrownItem.setNoGravity(false);

            level().addFreshEntity(thrownItem);
            this.playSound(SoundEvents.LLAMA_SPIT, 1.0F, 1.0F);
        }
    }

    public void openChest(Player player) {
        if (!this.isOpened()) {
            this.entityData.set(DATA_OPENED_ID, true);
            onOpen(player.getUUID());
        }
    }

    public void onOpen(UUID openedPlayerUUID) {
        if (level().getPlayerByUUID(openedPlayerUUID) instanceof ServerPlayer player) {
            this.targetPlayerUUID = openedPlayerUUID;

            level().playSound(null, this.blockPosition(), SoundEvents.CHEST_LOCKED, SoundSource.HOSTILE, 1.0F, 0.8F);

            this.setTarget(player);
            this.setNoAi(false);
            this.setAggressive(true);

            onTransformToMonster(player);
        }
    }

    protected abstract void onTransformToMonster(Player player);
    protected abstract void onKilledByPlayer(Player player);

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (isOpened() && targetPlayerUUID != null && this.getTarget() == null) {
            Player player = level().getPlayerByUUID(targetPlayerUUID);
            if (player != null && player.isAlive()) {
                this.setTarget(player);
            }
        }

        if (isOpened() && this.getTarget() != null && !storedItems.isEmpty()) {
            if (attackCooldown <= 0) {
                double distance = this.distanceTo(this.getTarget());
                if (distance > 3.0 && distance < 16.0 && this.random.nextInt(10) == 0) {
                    shootStoredItem();
                    attackCooldown = 20;
                }
            } else {
                attackCooldown--;
            }
        }


    }
}