package com.phantom.originiumarts.entity.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.projectile.EtchedAmmo
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.resources.ResourceLocation


class EtchedAmmoModel(part: ModelPart) : EntityModel<EtchedAmmo>() {

    companion object {
        val LAYER = ModelLayerLocation(ResourceLocation(OriginiumArtsMod.MOD_ID, "etched_ammo"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root
            partDefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create()
                    .texOffs(0, 4).addBox(-1.0f, -1.0f, -7.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-1.5f, -1.5f, -5.0f, 3.0f, 3.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(0, 12).addBox(-1.0f, -1.0f, 3.5f, 2.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-1.5f, -1.5f, 4.5f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 32, 32)
        }
    }

    private var bbMain: ModelPart

    init {
        bbMain = part.getChild("bb_main")
    }

    override fun setupAnim(
        entity: EtchedAmmo,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
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
        bbMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }
}