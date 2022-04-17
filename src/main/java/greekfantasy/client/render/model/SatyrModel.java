package greekfantasy.client.render.model;

import greekfantasy.entity.SatyrEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SatyrModel<T extends SatyrEntity> extends HoofedBipedModel<T> {

    private final ModelRenderer rightEar;
    private final ModelRenderer leftEar;

    public SatyrModel(float modelSize) {
        super(modelSize, true, true);

        // nose
        this.head.texOffs(24, 0).addBox(-1.0F, -3.0F, -5.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        // right ear
        rightEar = new ModelRenderer(this, 56, 16);
        rightEar.setPos(-3.0F, -4.0F, -1.0F);
        rightEar.xRot = -0.2618F;
        rightEar.yRot = -0.3491F;
        rightEar.addBox(-1.5F, -1.0F, 0.0F, 1.0F, 2.0F, 3.0F, modelSize);
        this.head.addChild(rightEar);

        // left ear
        leftEar = new ModelRenderer(this, 56, 22);
        leftEar.setPos(4.0F, -4.0F, -1.0F);
        leftEar.xRot = -0.2618F;
        leftEar.yRot = 0.3491F;
        leftEar.addBox(-0.5F, -1.0F, 0.0F, 1.0F, 2.0F, 3.0F, modelSize);
        this.head.addChild(leftEar);

        // horns
        this.head.addChild(makeGoatHorns(this, modelSize, true));
        this.head.addChild(makeGoatHorns(this, modelSize, false));
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.holdingPanfluteTime > 0) {
            // set arm rotations when holding panflute
            final float armPercent = entity.getArmMovementPercent(ageInTicks);
            this.rightArm.xRot = -1.31F * armPercent;
            this.rightArm.yRot = -0.68F * armPercent;
            this.leftArm.xRot = -1.22F * armPercent;
            this.leftArm.yRot = -0.43F * armPercent;
            this.leftArm.zRot = 1.17F * armPercent;
        }
    }

    public ModelRenderer getRightArm() {
        return this.rightArm;
    }

    public static ModelRenderer makeGoatHorns(final EntityModel<?> model, final float modelSize, final boolean isLeft) {
        final int textureX = isLeft ? 54 : 47;
        final float horn1X = isLeft ? 4.0F : -5.0F;
        final float horn2X = isLeft ? 8.25F : -1.25F;
        final float horn3X = isLeft ? 8.5F : -1.5F;

        final ModelRenderer horn3 = new ModelRenderer(model);
        horn3.setPos(0.0F, -3.0F, 0.0F);
        horn3.xRot = -0.7854F;
        horn3.texOffs(textureX, 59).addBox(horn3X, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, modelSize);
        horn3.mirror = isLeft;

        final ModelRenderer horn2 = new ModelRenderer(model);
        horn2.setPos(-4.0F, -4.0F, -1.0F);
        horn2.xRot = -0.7854F;
        horn2.texOffs(textureX, 54).addBox(horn2X, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, modelSize);
        horn2.addChild(horn3);
        horn2.mirror = isLeft;

        final ModelRenderer horn1 = new ModelRenderer(model);
        horn1.setPos(0.0F, -6.0F, -1.0F);
        horn1.xRot = 0.8727F;
        horn1.texOffs(textureX, 48).addBox(horn1X, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F, modelSize);
        horn1.addChild(horn2);
        horn1.mirror = isLeft;

        return horn1;
    }
}
