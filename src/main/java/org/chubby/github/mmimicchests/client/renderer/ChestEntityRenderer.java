package org.chubby.github.mmimicchests.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.chubby.github.mmimicchests.Constants;
import org.chubby.github.mmimicchests.client.model.ChestEntityModel;
import org.chubby.github.mmimicchests.entity.ChestEntity;
import org.jetbrains.annotations.NotNull;

public class ChestEntityRenderer extends MobRenderer<ChestEntity, ChestEntityModel> {

    public ChestEntityRenderer(EntityRendererProvider.Context context) {
        super(context,new ChestEntityModel(context.bakeLayer(ChestEntityModel.LOCATION)),0.5f);
    }

    @Override
    public void render(ChestEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.translate(0, -entity.getBbHeight(), 0);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();

    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ChestEntity chestEntity) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,"textures/entity/chest_entity.png");
    }
}
