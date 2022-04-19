package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.HandSide;

public class GiganteModel<T extends CreatureEntity> extends BipedModel<T> {

    public GiganteModel(float modelSize) {
        super(modelSize, 0.0F, 128, 64);

        head = new ModelRenderer(this);
        head.setPos(0.0F, -12.0F, 3.0F);
        head.texOffs(0, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize);
        head.texOffs(30, 0).addBox(-5.0F, 4.0F, -5.5F, 10.0F, 7.0F, 0.0F, modelSize);

        hat = new ModelRenderer(this);
        hat.setPos(0.0F, -12.0F, 3.0F);
        hat.texOffs(40, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize + 0.5F);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 24.0F, 0.0F);
        body.texOffs(0, 20).addBox(-6.0F, -32.0F, 0.0F, 12.0F, 16.0F, 6.0F, modelSize);

        leftArm = new ModelRenderer(this);
        leftArm.setPos(6.0F, -6.0F, 3.0F);
        leftArm.texOffs(64, 20).addBox(0.0F, -2.0F, -3.0F, 6.0F, 16.0F, 6.0F, modelSize);

        rightArm = new ModelRenderer(this);
        rightArm.setPos(-6.0F, -6.0F, 3.0F);
        rightArm.texOffs(40, 20).addBox(-6.0F, -2.0F, -3.0F, 6.0F, 16.0F, 6.0F, modelSize);

        leftLeg = new ModelRenderer(this);
        leftLeg.setPos(3.0F, 8.0F, 3.0F);
        leftLeg.texOffs(64, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, modelSize);
        leftLeg.mirror = true;

        rightLeg = new ModelRenderer(this);
        rightLeg.setPos(-3.0F, 8.0F, 3.0F);
        rightLeg.texOffs(40, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, modelSize);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
        head.setPos(0.0F, -12.0F, 3.0F);
        hat.setPos(0.0F, -12.0F, 3.0F);
        body.setPos(0.0F, 24.0F, 0.0F);
        leftArm.setPos(6.0F, -6.0F, 3.0F);
        rightArm.setPos(-6.0F, -6.0F, 3.0F);
        leftLeg.setPos(3.0F, 8.0F, 3.0F);
        rightLeg.setPos(-3.0F, 8.0F, 3.0F);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
                               float green, float blue, float alpha) {
        // this corrects a model offset mistake :/
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 0, -0.25D);
        super.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
    }

    @Override
    public void translateToHand(final HandSide sideIn, final MatrixStack matrixStackIn) {
        float dX = sideIn == HandSide.RIGHT ? -2F : 2F;
        float dZ = -3.5F;
        float dY = 3.0F;
        ModelRenderer armModel = this.getArm(sideIn);
        armModel.x += dX;
        armModel.y += dY;
        armModel.z += dZ;
        armModel.translateAndRotate(matrixStackIn);
        armModel.x -= dX;
        armModel.y -= dY;
        armModel.z -= dZ;
    }
}
