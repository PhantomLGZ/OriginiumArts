package com.phantom.originiumarts.entity.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.Rioter
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import kotlin.math.PI


class RioterModel(root: ModelPart) : HumanoidModel<Rioter>(root) {

    override fun setupAnim(
        pEntity: Rioter,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        super.setupAnim(pEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
        val flag = pEntity.fallFlyingTicks > 4
        val flag1 = pEntity.isVisuallySwimming
        head.yRot = netHeadYaw * (Math.PI.toFloat() / 360f)
        if (flag) {
            head.xRot = -Math.PI.toFloat() / 4f
        } else if (swimAmount > 0.0f) {
            if (flag1) {
                head.xRot = rotlerpRad(swimAmount, head.xRot, -PI.toFloat() / 4f)
            } else {
                head.xRot = rotlerpRad(swimAmount, head.xRot, headPitch * PI.toFloat() / 360f)
            }
        } else {
            head.xRot = headPitch * (Math.PI.toFloat() / 360f)
        }
        hat.copyFrom(head)
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        val LAYER = ModelLayerLocation(ResourceLocation(OriginiumArtsMod.MOD_ID, "rioter"), "main")
        fun createBodyLayer(): LayerDefinition? {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val hat = partdefinition.addOrReplaceChild(
                "hat",
                CubeListBuilder.create().texOffs(2, 0)
                    .addBox(-3.99f, -1.8684f, -5.9391f, 0.0f, 5.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(3.99f, -1.8684f, -5.9391f, 0.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -0.5f)
            )
            val cube_r1 = hat.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(32, 18)
                    .addBox(-4.0f, 5.0933f, -0.7074f, 8.0f, 0.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 17).addBox(-4.0f, -3.9067f, -4.7074f, 8.0f, 0.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(0, 17).addBox(4.01f, -3.9067f, -4.7074f, 0.0f, 9.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(18, 24).addBox(-4.01f, -3.9067f, -4.7074f, 0.0f, 9.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(43, 23).addBox(-4.0f, -3.9067f, 4.2926f, 8.0f, 9.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.7746f, -1.5092f, 0.4363f, 0.0f, 0.0f)
            )
            val head =
                partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))
            val cube_r2 = head.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(19, 20)
                    .addBox(-3.0f, -6.5f, -3.5f, 6.0f, 7.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.8978f, -0.2765f, 0.4363f, 0.0f, 0.0f)
            )
            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-5.0f, 0.0f, -3.0f, 10.0f, 11.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )
            val right_arm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(16, 42)
                    .addBox(-3.0f, -1.5f, -2.0f, 3.0f, 13.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 1.5f, 0.0f)
            )
            val left_arm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(36, 33)
                    .addBox(0.0f, -1.5f, -2.0f, 3.0f, 13.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 1.5f, 0.0f)
            )
            val right_leg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 35)
                    .addBox(-2.0f, -1.0f, -2.0f, 4.0f, 14.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.5f, 11.0f, 0.0f)
            )
            val left_leg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(32, 0)
                    .addBox(-2.0f, -1.0f, -2.0f, 4.0f, 14.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.5f, 11.0f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}