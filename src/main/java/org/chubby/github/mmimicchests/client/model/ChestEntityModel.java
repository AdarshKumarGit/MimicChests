package org.chubby.github.mmimicchests.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.chubby.github.mmimicchests.Constants;
import org.chubby.github.mmimicchests.entity.ChestEntity;

public class ChestEntityModel extends EntityModel<ChestEntity> {

    public static final ModelLayerLocation LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,"chest_body"),"main");
    private final ModelPart main;
    private final ModelPart chest;
    private final ModelPart legs;
    private final ModelPart right;
    private final ModelPart left;

    public ChestEntityModel(ModelPart root) {
        this.main = root.getChild("main");
        this.chest = this.main.getChild("chest");
        this.legs = this.main.getChild("legs");
        this.right = this.legs.getChild("right");
        this.left = this.legs.getChild("left");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition chest = main.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 19).addBox(-7.0F, -8.0F, -7.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(6, 56).addBox(-7.0F, 9.0F, 3.75F, 14.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        PartDefinition cube_r1 = chest.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(10, 50).mirror().addBox(-7.0F, -1.0F, 0.0F, 14.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 5.7335F, -0.9949F, -1.5708F, 0.8727F, -1.5708F));

        PartDefinition cube_r2 = chest.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(14, 46).addBox(-7.0F, -1.0F, 0.0F, 14.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 5.7335F, -0.9949F, -1.5708F, -0.8727F, 1.5708F));

        PartDefinition cube_r3 = chest.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -1.25F, -10.75F, 14.0F, 5.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, -3.25F, 3.25F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.8675F, 0.4315F, -0.6981F, 0.0F, 0.0F));

        PartDefinition legs = main.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

        PartDefinition right = legs.addOrReplaceChild("right", CubeListBuilder.create().texOffs(17, 21).addBox(-1.0F, -3.5F, -2.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 4).addBox(-1.0F, 0.5F, 0.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 6.5F, 1.0F));

        PartDefinition left = legs.addOrReplaceChild("left", CubeListBuilder.create().texOffs(17, 22).addBox(-1.0F, -3.5F, -2.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(17, 24).addBox(-1.0F, 0.5F, 0.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 6.5F, 1.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(ChestEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.main.y = 24.0F;
        this.right.xRot = 0;
        this.left.xRot = 0;
        this.chest.xRot = 0;
        this.chest.zRot = -3.1416F;

        if (limbSwingAmount > 0.01F) {
            float walkSpeed = 1.0F;
            float walkDegree = 1.4F;

            this.right.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.left.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;

            float bobHeight = 1.129F * limbSwingAmount;
            this.main.y = 24.0F + Mth.cos(limbSwing * walkSpeed * 2.0F) * bobHeight;

            // Chest body wobble
            float wobbleAmount = 0.08F * limbSwingAmount;
            this.chest.xRot = Mth.sin(limbSwing * walkSpeed) * wobbleAmount;
            this.chest.zRot = -3.1416F + Mth.cos(limbSwing * walkSpeed * 0.5F) * wobbleAmount;
        }

        if (entity.isOpened()) {
            float idleBob = 0.05F * (Mth.sin(ageInTicks * 0.1F) + 1.0F);
            this.chest.xRot = 0.15F + idleBob;

            if (limbSwingAmount < 0.01F) {
                float breatheAmount = 0.03F;
                this.main.y = 24.0F + Mth.sin(ageInTicks * 0.1F) * breatheAmount;
                this.chest.zRot = -3.1416F + Mth.sin(ageInTicks * 0.05F) * breatheAmount;
            }
        }

        if (entity.isAggressive() && entity.getAttackTick() > 0) {
            float attackProgress = Math.min(1.0F, entity.getAttackTick() / 10.0F);
            float attackBounce = Mth.sin((1.0F - (1.0F - attackProgress) * (1.0F - attackProgress)) * (float)Math.PI);

            this.main.y = 24.0F - attackBounce * 2.0F;

            this.chest.xRot = 0.2F + attackBounce * 0.2F;
        }
    }


    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}