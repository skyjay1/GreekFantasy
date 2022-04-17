package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import greekfantasy.entity.HydraEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class HydraModel<T extends HydraEntity> extends EntityModel<T> {

    private final ModelRenderer body;
    private final ModelRenderer bodyUpper;
    private final ModelRenderer bodyMid;
    private final ModelRenderer bodyLower1;
    private final ModelRenderer bodyLower2;
    private final ModelRenderer bodyLower3;
    private final ModelRenderer bodyLower4;
    private final ModelRenderer bodyLower5;

    public HydraModel() {
        super();
        texWidth = 128;
        texHeight = 64;

        body = new ModelRenderer(this);
        body.setPos(0.0F, 2.0F, -9.0F);


        bodyUpper = new ModelRenderer(this);
        bodyUpper.setPos(0.0F, 0.0F, 0.0F);
        bodyUpper.xRot = 0.2618F;
        body.addChild(bodyUpper);


        ModelRenderer bodyRight = new ModelRenderer(this);
        bodyRight.setPos(0.0F, 2.0F, -6.0F);
        bodyRight.yRot = 0.2618F;
        bodyRight.texOffs(0, 0).addBox(-14.0F, -7.0F, 0.0F, 14.0F, 12.0F, 6.0F, 0.0F, true);
        bodyUpper.addChild(bodyRight);

        ModelRenderer bodyLeft = new ModelRenderer(this);
        bodyLeft.setPos(0.0F, 2.0F, -6.0F);
        bodyLeft.yRot = -0.2618F;
        bodyLeft.texOffs(0, 0).addBox(0.0F, -7.0F, 0.0F, 14.0F, 12.0F, 6.0F, 0.0F, false);
        bodyUpper.addChild(bodyLeft);

        ModelRenderer backLeft = new ModelRenderer(this);
        backLeft.setPos(0.0F, 2.0F, 6.5F);
        backLeft.yRot = 0.2618F;
        backLeft.texOffs(42, 0).addBox(0.0F, -7.01F, -6.0F, 12.0F, 12.0F, 6.0F, 0.0F, false);
        backLeft.texOffs(120, 0).addBox(2.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, 0.0F, false);
        backLeft.texOffs(120, 0).addBox(6.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, 0.0F, false);
        backLeft.texOffs(120, 0).addBox(10.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, 0.0F, false);
        bodyUpper.addChild(backLeft);

        ModelRenderer backRight = new ModelRenderer(this);
        backRight.setPos(0.0F, 2.0F, 6.5F);
        backRight.yRot = -0.2618F;
        backRight.texOffs(42, 0).addBox(-12.0F, -7.01F, -6.0F, 12.0F, 12.0F, 6.0F, 0.0F, true);
        backRight.texOffs(120, 0).addBox(-3.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, 0.0F, true);
        backRight.texOffs(120, 0).addBox(-7.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, 0.0F, true);
        backRight.texOffs(120, 0).addBox(-11.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, 0.0F, true);
        bodyUpper.addChild(backRight);

        bodyMid = new ModelRenderer(this);
        bodyMid.setPos(0.0F, 7.0F, -4.0F);
        bodyMid.xRot = 0.2618F;
        bodyUpper.addChild(bodyMid);


        ModelRenderer bodyRight2 = new ModelRenderer(this);
        bodyRight2.setPos(0.0F, 7.0F, -1.0F);
        bodyRight2.yRot = 0.2618F;
        bodyRight2.texOffs(0, 20).addBox(-10.0F, -8.0F, 0.0F, 10.0F, 12.0F, 6.0F, 0.0F, true);
        bodyMid.addChild(bodyRight2);

        ModelRenderer bodyLeft2 = new ModelRenderer(this);
        bodyLeft2.setPos(0.0F, 7.0F, -1.0F);
        bodyLeft2.yRot = -0.2618F;
        bodyLeft2.texOffs(0, 20).addBox(0.0F, -8.0F, 0.0F, 10.0F, 12.0F, 6.0F, 0.0F, false);
        bodyMid.addChild(bodyLeft2);

        ModelRenderer backLeft2 = new ModelRenderer(this);
        backLeft2.setPos(0.0F, 7.0F, 9.0F);
        backLeft2.yRot = 0.2182F;
        backLeft2.texOffs(34, 20).addBox(0.0F, -8.01F, -4.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);
        backLeft2.texOffs(120, 0).addBox(1.0F, -4.0F, 0.0F, 1.0F, 8.0F, 3.0F, 0.0F, false);
        backLeft2.texOffs(120, 0).addBox(5.0F, -4.0F, 0.0F, 1.0F, 8.0F, 3.0F, 0.0F, false);
        bodyMid.addChild(backLeft2);

        ModelRenderer backRight2 = new ModelRenderer(this);
        backRight2.setPos(0.0F, 7.0F, 9.0F);
        backRight2.yRot = -0.2182F;
        backRight2.texOffs(34, 20).addBox(-8.0F, -8.01F, -4.0F, 8.0F, 12.0F, 4.0F, 0.0F, true);
        backRight2.texOffs(120, 0).addBox(-2.0F, -4.0F, 0.0F, 1.0F, 8.0F, 3.0F, 0.0F, true);
        backRight2.texOffs(120, 0).addBox(-6.0F, -4.0F, 0.0F, 1.0F, 8.0F, 3.0F, 0.0F, true);
        bodyMid.addChild(backRight2);

        bodyLower1 = new ModelRenderer(this);
        bodyLower1.setPos(0.0F, 11.0F, 0.0F);
        bodyLower1.xRot = 0.4363F;
        bodyLower1.texOffs(0, 40).addBox(-6.0F, 0.0F, 1.0F, 12.0F, 10.0F, 6.0F, 0.0F, false);
        bodyLower1.texOffs(120, 0).addBox(0.0F, 3.0F, 7.0F, 1.0F, 7.0F, 3.0F, 0.0F, false);
        bodyMid.addChild(bodyLower1);

        bodyLower2 = new ModelRenderer(this);
        bodyLower2.setPos(0.0F, 10.0F, 1.0F);
        bodyLower2.xRot = 0.6109F;
        bodyLower2.texOffs(84, 0).addBox(-5.0F, 0.0F, 0.01F, 10.0F, 10.0F, 5.0F, 0.0F, false);
        bodyLower2.texOffs(120, 0).addBox(0.0F, 3.0F, 5.01F, 1.0F, 7.0F, 3.0F, 0.0F, false);
        bodyLower1.addChild(bodyLower2);

        bodyLower3 = new ModelRenderer(this);
        bodyLower3.setPos(0.0F, 9.0F, 0.0F);
        bodyLower3.texOffs(84, 16).addBox(-4.0F, 0.0F, 0.01F, 8.0F, 10.0F, 5.0F, 0.0F, false);
        bodyLower3.texOffs(120, 0).addBox(0.0F, 0.0F, 5.0F, 1.0F, 10.0F, 3.0F, 0.0F, false);
        bodyLower2.addChild(bodyLower3);

        bodyLower4 = new ModelRenderer(this);
        bodyLower4.setPos(0.0F, 9.0F, 0.0F);
        bodyLower4.texOffs(84, 32).addBox(-3.0F, 0.0F, 0.01F, 6.0F, 10.0F, 5.0F, 0.0F, false);
        bodyLower4.texOffs(120, 0).addBox(0.0F, 0.0F, 5.01F, 1.0F, 10.0F, 3.0F, 0.0F, false);
        bodyLower3.addChild(bodyLower4);

        bodyLower5 = new ModelRenderer(this);
        bodyLower5.setPos(0.0F, 9.0F, 0.0F);
        bodyLower5.texOffs(84, 48).addBox(-2.0F, 0.0F, 0.01F, 4.0F, 10.0F, 4.0F, 0.0F, false);
        bodyLower5.texOffs(120, 16).addBox(0.0F, 0.0F, 4.01F, 1.0F, 10.0F, 3.0F, 0.0F, false);
        bodyLower4.addChild(bodyLower5);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        // animate snake body
        final float idleSwingCos = MathHelper.cos((entityIn.tickCount + partialTick) * 0.12F);
        final float limbSwingCos = MathHelper.cos(limbSwing);
        // limb swing rotations
        bodyUpper.yRot = limbSwingCos * 0.04F + idleSwingCos * 0.011F;
        bodyMid.yRot = limbSwingCos * -0.12F + idleSwingCos * 0.011F;
        bodyLower1.yRot = idleSwingCos * -0.022F;
        bodyLower1.zRot = limbSwingCos * 0.1F;
        bodyLower2.zRot = limbSwingCos * -0.37F;
        bodyLower3.zRot = limbSwingCos * 0.77F;
        bodyLower4.zRot = limbSwingCos * -0.95F;
        bodyLower5.zRot = limbSwingCos * 0.72F;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
                               float green, float blue, float alpha) {
        body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }
}
