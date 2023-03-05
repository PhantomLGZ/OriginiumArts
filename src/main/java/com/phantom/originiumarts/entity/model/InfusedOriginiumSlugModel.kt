package com.phantom.originiumarts.entity.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.InfusedOriginiumSlug
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.resources.ResourceLocation
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class InfusedOriginiumSlugModel(root: ModelPart) : EntityModel<InfusedOriginiumSlug>() {

    private val body: ModelPart
    private val b3: ModelPart
    private val leg_r1: ModelPart
    private val leg_l1: ModelPart
    private val leg_r2: ModelPart
    private val leg_l2: ModelPart
    private val leg_r3: ModelPart
    private val leg_l3: ModelPart
    private val head: ModelPart

    init {
        body = root.getChild("body")
        b3 = body.getChild("b3_r1")
        head = root.getChild("head")
        leg_r1 = head.getChild("leg_r1")
        leg_l1 = head.getChild("leg_l1")
        leg_r2 = head.getChild("leg_r2")
        leg_l2 = head.getChild("leg_l2")
        leg_r3 = head.getChild("leg_r3")
        leg_l3 = head.getChild("leg_l3")
    }

    override fun setupAnim(
        entity: InfusedOriginiumSlug,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        head.yRot = netHeadYaw * (PI.toFloat() / 360f)
        (sin(limbSwing * 1.5f * 0.5f) * 2.0f * limbSwingAmount).let {
            leg_r1.yRot = it + 0.4f
            leg_l2.yRot = it
            leg_r3.yRot = it
        }
        (cos(limbSwing * 1.5f * 0.5f) * 2.0f * limbSwingAmount).let {
            leg_l1.yRot = it - 0.4f
            leg_r2.yRot = it
            leg_l3.yRot = it
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
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        val LAYER = ModelLayerLocation(ResourceLocation(OriginiumArtsMod.MOD_ID, "infused_originium_slug"), "main")

        fun createBodyLayer(): LayerDefinition? {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            val body: PartDefinition =
                partDefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0f, 21.75f, -1.0f))

            body.addOrReplaceChild(
                "b3_r1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -2.2234f, -2.2057f, 6.0f, 4.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.0f, 5.0f, 2.5307f, 0.0f, 3.1416f)
            )

            body.addOrReplaceChild(
                "b2_r1",
                CubeListBuilder.create().texOffs(14, 15)
                    .addBox(-2.0f, -7.0f, -4.0f, 4.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 2.0f, 5.0f, 2.5307f, 0.0f, 3.1416f)
            )

            body.addOrReplaceChild(
                "b1_r1",
                CubeListBuilder.create().texOffs(0, 9)
                    .addBox(-1.5f, -4.0f, 0.0f, 3.0f, 4.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 2.0f, 5.0f, -2.7925f, 0.0f, -3.1416f)
            )

            val head =
                partDefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0f, 21.0f, -1.0f))

            head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(17, 4)
                    .addBox(-1.0f, -0.7389f, -0.4829f, 2.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.75f, 0.0f, 3.0107f, 0.0f, 3.1416f)
            )

            val leg_r1 = head.addOrReplaceChild(
                "leg_r1",
                CubeListBuilder.create().texOffs(20, 11)
                    .addBox(-0.5f, -2.5f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.6376f, -0.6365f, -3.1106f, 0.4976f, 0.677f, -0.1789f)
            )

            leg_r1.addOrReplaceChild(
                "r1_d_r1",
                CubeListBuilder.create().texOffs(7, 26)
                    .addBox(-0.5f, 0.0f, -1.0f, 1.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.5f, 0.5f, -0.9599f, 0.0f, 0.0f)
            )

            val leg_l1 = head.addOrReplaceChild(
                "leg_l1",
                CubeListBuilder.create().texOffs(17, 0)
                    .addBox(-0.5f, -2.5f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.6376f, -0.6365f, -3.1106f, 0.4976f, -0.677f, 0.1789f)
            )

            leg_l1.addOrReplaceChild(
                "l1_d_r1",
                CubeListBuilder.create().texOffs(26, 0)
                    .addBox(-0.5f, 0.0f, -1.0f, 1.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.5f, 0.5f, -0.9599f, 0.0f, 0.0f)
            )

            val leg_r2 = head.addOrReplaceChild(
                "leg_r2",
                CubeListBuilder.create().texOffs(16, 11)
                    .addBox(-0.5f, -2.5f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.4848f, -0.5094f, -1.9484f, 0.0114f, -0.1795f, -0.6157f)
            )

            leg_r2.addOrReplaceChild(
                "r2_d_r1",
                CubeListBuilder.create().texOffs(11, 25)
                    .addBox(-1.0f, 0.0f, -0.5f, 1.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.5f, -2.5f, 0.0f, 0.0f, 0.0f, 1.1345f)
            )

            val leg_l2 = head.addOrReplaceChild(
                "leg_l2",
                CubeListBuilder.create().texOffs(12, 9)
                    .addBox(-0.5f, -2.5f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.4848f, -0.5094f, -1.9484f, 0.0114f, 0.1795f, 0.6157f)
            )

            leg_l2.addOrReplaceChild(
                "l2_d_r1",
                CubeListBuilder.create().texOffs(8, 19)
                    .addBox(0.0f, 0.0f, -0.5f, 1.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.5f, -2.5f, 0.0f, 0.0f, 0.0f, -1.1345f)
            )

            val leg_r3 = head.addOrReplaceChild(
                "leg_r3",
                CubeListBuilder.create().texOffs(0, 9)
                    .addBox(-0.5f, -2.5f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.7f, -1.0f, -0.6f, -0.2182f, 0.5672f, -0.3491f)
            )

            leg_r3.addOrReplaceChild(
                "r3_d_r1",
                CubeListBuilder.create().texOffs(4, 19)
                    .addBox(-1.0f, 0.0f, -0.5f, 1.0f, 7.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.5f, -2.5f, 0.0f, 0.0f, 0.0f, 0.8727f)
            )

            val leg_l3 = head.addOrReplaceChild(
                "leg_l3",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-0.5f, -2.5f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.7f, -1.0f, -0.6f, -0.2182f, -0.5672f, 0.3491f)
            )

            leg_l3.addOrReplaceChild(
                "l3_d_r1",
                CubeListBuilder.create().texOffs(0, 19)
                    .addBox(0.0f, 0.0f, -0.5f, 1.0f, 7.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.5f, -2.5f, 0.0f, 0.0f, 0.0f, -0.8727f)
            )

            return LayerDefinition.create(meshDefinition, 64, 64)
        }
    }

}