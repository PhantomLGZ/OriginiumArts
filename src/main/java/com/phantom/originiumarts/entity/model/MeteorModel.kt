package com.phantom.originiumarts.entity.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.projectile.Meteor
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.resources.ResourceLocation


class MeteorModel(root: ModelPart) : EntityModel<Meteor>() {

    private val bb_main: ModelPart

    init {
        bb_main = root.getChild("bb_main")
    }

    companion object {
        val LAYER = ModelLayerLocation(ResourceLocation(OriginiumArtsMod.MOD_ID, "meteor"), "main")

        fun createBodyLayer(): LayerDefinition? {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            partDefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create()
                    .texOffs(28, 6).addBox(-2.0f, -2.0f, -6.0f, 4.0f, 4.0f, 12.0f, CubeDeformation(0.0f))
                    .texOffs(0, 40).addBox(-6.0f, -2.0f, -2.0f, 12.0f, 4.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(28, 44).addBox(-2.0f, -6.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-4.0f, -4.0f, -5.0f, 8.0f, 8.0f, 10.0f, CubeDeformation(0.0f))
                    .texOffs(0, 18).addBox(-5.0f, -4.0f, -4.0f, 10.0f, 8.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(28, 26).addBox(-4.0f, -5.0f, -4.0f, 8.0f, 10.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
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
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    override fun setupAnim(
        pEntity: Meteor,
        pLimbSwing: Float,
        pLimbSwingAmount: Float,
        pAgeInTicks: Float,
        pNetHeadYaw: Float,
        pHeadPitch: Float
    ) {

    }


}