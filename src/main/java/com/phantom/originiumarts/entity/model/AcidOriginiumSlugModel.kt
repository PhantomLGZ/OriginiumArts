package com.phantom.originiumarts.entity.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.AcidOriginiumSlug
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt

class AcidOriginiumSlugModel(root: ModelPart) : EntityModel<AcidOriginiumSlug>() {
    private val head: ModelPart
    private val body: ModelPart

    init {
        head = root.getChild("head")
        body = root.getChild("body")
    }

    override fun setupAnim(
        entity: AcidOriginiumSlug,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        if (entity.getAttackTick() < 20) {
            head.xRot =
                (sin(sqrt(entity.getAttackTick() / 20.0) * PI * 2) * PI / 12).toFloat() + headPitch * (PI.toFloat() / 180f)
        } else {
            head.xRot = headPitch * (PI.toFloat() / 180f)
        }
        head.yRot = netHeadYaw * (PI.toFloat() / 360f)
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
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        val LAYER = ModelLayerLocation(ResourceLocation(OriginiumArtsMod.MOD_ID, "acid_originium_slug"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 23.0f, -3.0f)
            )
            head.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 6)
                    .addBox(-1.5f, 4.0f, -8.0f, 1.0f, 1.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(30, 5).addBox(0.5f, 4.0f, -8.0f, 1.0f, 1.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 2.0f, -1.1781f, 0.0f, 0.0f)
            )
            head.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(0, 23)
                    .addBox(-0.5f, -0.5f, -8.0f, 1.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(29, 13).addBox(-1.5f, -1.0f, -7.0f, 3.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 2.0f, -0.5672f, 0.0f, 0.0f)
            )
            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(25, 24)
                    .addBox(-3.0f, -7.0f, -2.0f, 6.0f, 2.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-5.0f, -5.0f, -4.0f, 10.0f, 3.0f, 10.0f, CubeDeformation(0.0f))
                    .texOffs(0, 23).addBox(-4.5f, -2.0f, -2.0f, 9.0f, 1.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(0, 13).addBox(-5.0f, -1.0f, -3.0f, 10.0f, 1.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(30, 0).addBox(-2.5f, -2.0f, -5.0f, 5.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            body.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(5, 6)
                    .addBox(-1.5f, -9.0f, -3.0f, 1.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -1.3963f, -0.2182f, 0.0f)
            )
            body.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(0, 19)
                    .addBox(0.5f, -9.0f, -3.0f, 1.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -1.3963f, 0.2182f, 0.0f)
            )
            body.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(0, 31)
                    .addBox(-1.5f, -11.0f, -1.0f, 3.0f, 4.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.4363f, 0.0f, 0.0f)
            )
            body.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(4, 19)
                    .addBox(-0.5f, -8.0f, 0.5f, 1.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.1745f, 0.0f, 0.0f)
            )
            body.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(1.0f, -9.0f, -1.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.3054f, 0.0f, 0.0873f)
            )
            body.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create().texOffs(0, 13)
                    .addBox(-3.0f, -9.0f, -1.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.3054f, 0.0f, -0.0873f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}