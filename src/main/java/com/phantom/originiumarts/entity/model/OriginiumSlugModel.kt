package com.phantom.originiumarts.entity.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.OriginiumSlug
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.resources.ResourceLocation


class OriginiumSlugModel(part: ModelPart) : EntityModel<OriginiumSlug>() {

    private val body: ModelPart

    init {
        body = part.getChild("body")
    }

    companion object {
        val LAYER = ModelLayerLocation(ResourceLocation(OriginiumArtsMod.MOD_ID, "originium_slug"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            val body: PartDefinition = partDefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                    .texOffs(6, 23).addBox(-1.5f, 1.4375f, 2.8125f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(18, 19).addBox(-3.0f, 0.4375f, 0.8125f, 6.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-4.0f, -1.5625f, -2.6875f, 8.0f, 4.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(0, 14).addBox(-2.5f, -2.5625f, -2.6875f, 5.0f, 1.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 20).addBox(-3.0f, -1.5625f, -4.1875f, 6.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 8).addBox(-3.5f, -2.0625f, -2.1875f, 7.0f, 1.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(19, 8).addBox(-3.5f, -1.0625f, 1.3125f, 7.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(15, 14).addBox(-3.5f, -0.5625f, -4.6875f, 7.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 21.5625f, -0.3125f, 0.0f, 3.1416f, 0.0f)
            )

            body.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 23)
                    .addBox(-1.0f, -0.5f, -1.0f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.6157f, 3.041f, -1.2654f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(15, 22)
                    .addBox(-0.5f, -1.5f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -1.2171f, 4.9485f, -1.2654f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(20, 0)
                    .addBox(-1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.2445f, 0.5587f, -0.48f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(0, 8)
                    .addBox(-0.5f, -2.0f, -0.5f, 1.0f, 4.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -4.9055f, 1.9439f, -0.48f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(0, 14)
                    .addBox(-0.5f, -1.5f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -1.8757f, -4.542f, 0.9163f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-0.5f, -1.5f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.139f, -5.7308f, 1.3526f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 64, 64)
        }
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
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    override fun setupAnim(
        entity: OriginiumSlug,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {}
}