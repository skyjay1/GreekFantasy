package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.Hand;

public class NymphModel<T extends MobEntity> extends BipedModel<T> {

    private final ModelRenderer bipedChest;

    public NymphModel(final float modelSize) {
        super(modelSize);

        head = new ModelRenderer(this);
        head.setPos(0.0F, 0.0F, 1.5F);
        head.texOffs(0, 0).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, modelSize);
        head.texOffs(21, 0).addBox(-3.5F, 0.0F, 2.5F, 7.0F, 6.0F, 1.0F, modelSize);

        // hide headwear
        hat.visible = false;

        body = new ModelRenderer(this, 0, 16);
        body.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 12.0F, 3.0F, modelSize);

        bipedChest = new ModelRenderer(this, 30, 7);
        bipedChest.setPos(0.0F, 1.0F, 1.0F);
        bipedChest.xRot = -0.1745F;
        bipedChest.addBox(-2.99F, 0.0F, -1.0F, 6.0F, 4.0F, 1.0F, modelSize);

        leftArm = new ModelRenderer(this, 19, 16);
        leftArm.setPos(3.0F, 2.0F, 1.5F);
        leftArm.addBox(0.0F, -2.0F, -1.5F, 2.0F, 12.0F, 3.0F, modelSize);

        rightArm = new ModelRenderer(this, 29, 16);
        rightArm.setPos(-3.0F, 2.0F, 1.5F);
        rightArm.addBox(-2.0F, -2.0F, -1.5F, 2.0F, 12.0F, 3.0F, modelSize);

        leftLeg = new ModelRenderer(this, 40, 16);
        leftLeg.setPos(1.5F, 12.0F, 1.5F);
        leftLeg.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, modelSize);

        rightLeg = new ModelRenderer(this, 52, 16);
        rightLeg.setPos(-1.5F, 12.0F, 1.5F);
        rightLeg.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, modelSize);
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.bipedChest));
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                          float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // correct rotation points
        head.setPos(0.0F, 0.0F, 1.5F);
        leftArm.setPos(3.0F, 2.0F, 1.5F);
        rightArm.setPos(-3.0F, 2.0F, 1.5F);
        leftLeg.setPos(1.5F, 12.0F, 1.5F);
        rightLeg.setPos(-1.5F, 12.0F, 1.5F);
        // held item
        if (!entityIn.getItemInHand(Hand.MAIN_HAND).isEmpty()) {
            this.getArm(this.getAttackArm(entityIn).getOpposite()).xRot += -0.42F;
        }
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
                               float green, float blue, float alpha) {
        // this corrects a model offset mistake :/
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 0, -0.125D);
        super.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
    }
}