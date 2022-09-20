package com.phantom.originiumarts.entity.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.DroneMonster
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


class DroneMonsterModel(val root: ModelPart) : EntityModel<DroneMonster>() {

    private var rotor: ModelPart = root.getChild("rotor")
    private var body: ModelPart = root.getChild("body")
    private var gun: ModelPart = root.getChild("gun")

    override fun setupAnim(
        entity: DroneMonster,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        gun.xRot = headPitch * (PI.toFloat() / 360f)
        gun.yRot = netHeadYaw * (PI.toFloat() / 720f)
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
        rotor.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        gun.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        val LAYER = ModelLayerLocation(ResourceLocation(OriginiumArtsMod.MOD_ID, "drone_monster"), "main")
        fun createBodyLayer(): LayerDefinition? {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val rotor = partdefinition.addOrReplaceChild(
                "rotor",
                CubeListBuilder.create().texOffs(10, 12)
                    .addBox(-7.0f, -3.8643f, -8.1495f, 5.0f, 0.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 12).addBox(-7.0f, -3.8643f, 0.8505f, 5.0f, 0.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(8, 0).addBox(2.0f, -3.8643f, -8.1495f, 5.0f, 0.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(8, 5).addBox(2.0f, -3.8643f, 0.8505f, 5.0f, 0.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(21, 17).addBox(4.0f, -3.8643f, 2.8505f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 19).addBox(-5.0f, -3.8643f, 2.8505f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(7, 7).addBox(-5.0f, -3.8643f, -6.1495f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(6, 9).addBox(4.0f, -3.8643f, -6.1495f, 1.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 18.8643f, 1.1495f)
            )
            rotor.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 12)
                    .addBox(-0.5f, -3.0f, -0.5f, 1.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.914f, -1.6938f, 1.5402f, -0.9827f, -0.7308f, 0.0169f)
            )
            rotor.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(16, 21)
                    .addBox(-0.5f, -3.0f, -0.5f, 1.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.914f, -1.6938f, 1.5402f, -0.9827f, 0.7308f, -0.0169f)
            )
            rotor.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-0.5f, -3.5f, -0.5f, 1.0f, 7.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.035f, -1.8492f, -2.9423f, 1.0742f, 0.4244f, -0.1258f)
            )
            rotor.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(4, 0)
                    .addBox(-0.5f, -3.5f, -0.5f, 1.0f, 7.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.035f, -1.8492f, -2.9423f, 1.0742f, -0.4244f, 0.1258f)
            )
            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 17)
                    .addBox(-2.0f, -6.0f, 0.5f, 4.0f, 3.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(12, 17).addBox(-1.5f, -7.0f, 1.0f, 3.0f, 1.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 8).addBox(-1.0f, -6.0f, -0.5f, 2.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(23, 0).addBox(2.0f, -5.5f, 2.0f, 1.0f, 3.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(20, 21).addBox(-3.0f, -5.5f, 2.0f, 1.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val gun =
                partdefinition.addOrReplaceChild("gun", CubeListBuilder.create(), PartPose.offset(0.0f, 20.5f, 1.8497f))
            gun.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-0.5f, -0.5f, -9.5f, 1.0f, 1.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.2618f, 0.0f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}