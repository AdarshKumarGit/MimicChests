package org.chubby.github.mmimicchests.entity;

import net.minecraft.client.particle.DustParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChestEntity extends AbstractChestEntity {

    private int attackAnimationTick;

    public ChestEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.attackAnimationTick = 0;
    }

    public static AttributeSupplier.Builder createAts() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.ATTACK_DAMAGE, 3.0F)
                .add(Attributes.MAX_HEALTH, 16.0F)
                .add(Attributes.ARMOR, 2.0F)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 1.0D, 16));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attackAnimationTick > 0) {
            this.attackAnimationTick--;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 0.001D) {
                for (int i = 0; i < 2; i++) {
                    double offsetX = (this.getRandom().nextDouble() - 0.5) * 0.5;
                    double offsetZ = (this.getRandom().nextDouble() - 0.5) * 0.5;

                    level().addParticle(
                            ParticleTypes.SMOKE,
                            this.getX() + offsetX,
                            this.getY() + 0.1,
                            this.getZ() + offsetZ,
                            0.0,
                            0.05 + this.getRandom().nextDouble() * 0.05,
                            0.0
                    );
                }
            }
        }
    }

    @Override
    public void shootStoredItem() {
        this.attackAnimationTick = 15;
        super.shootStoredItem();
    }

    public int getAttackTick() {
        return this.attackAnimationTick;
    }

    @Override
    protected void onTransformToMonster(Player player) {
        this.playSound(SoundEvents.ENDERMAN_SCREAM, 1.0F, 0.5F);
        this.attackAnimationTick = 20;
    }

    @Override
    protected void onKilledByPlayer(Player player) {
        this.playSound(SoundEvents.CHEST_CLOSE, 1.0F, 1.0F);
    }
}