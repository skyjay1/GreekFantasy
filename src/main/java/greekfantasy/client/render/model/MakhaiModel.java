package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import greekfantasy.entity.MakhaiEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class MakhaiModel<T extends MakhaiEntity> extends BipedModel<T> {

    public final ModelRenderer bipedHead2;
    public final ModelRenderer bipedRightArm2;
    public final ModelRenderer bipedRightArm3;
    public final ModelRenderer bipedLeftArm2;
    public final ModelRenderer bipedLeftArm3;

    public MakhaiModel(float modelSize) {
        super(modelSize);
        this.texWidth = 64;
        this.texHeight = 32;

        head = new ModelRenderer(this);
        head.setPos(-4.0F, 0.0F, 0.0F);
        head.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        hat = new ModelRenderer(this);
        hat.setPos(-4.0F, 0.0F, 0.0F);
        hat.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F, false);

        bipedHead2 = new ModelRenderer(this);
        bipedHead2.setPos(4.0F, 0.0F, 0.0F);
        bipedHead2.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        rightArm = new ModelRenderer(this);
        rightArm.setPos(-5.0F, 2.0F, 0.0F);
        rightArm.texOffs(40, 16).addBox(-3.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        bipedRightArm2 = new ModelRenderer(this);
        bipedRightArm2.setPos(-4.0F, 2.0F, 0.0F);
        bipedRightArm2.texOffs(40, 16).addBox(-3.0F, 0.0F, -3.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        bipedRightArm3 = new ModelRenderer(this);
        bipedRightArm3.setPos(-5.0F, 2.0F, 0.0F);
        bipedRightArm3.texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        leftArm = new ModelRenderer(this);
        leftArm.setPos(5.0F, 3.0F, 0.0F);
        leftArm.texOffs(40, 16).addBox(-1.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);

        bipedLeftArm2 = new ModelRenderer(this);
        bipedLeftArm2.setPos(4.0F, 2.0F, 0.0F);
        bipedLeftArm2.texOffs(40, 16).addBox(0.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);

        bipedLeftArm3 = new ModelRenderer(this);
        bipedLeftArm3.setPos(4.0F, 2.0F, 0.0F);
        bipedLeftArm3.texOffs(40, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 0.0F, 0.0F);
        body.texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

        rightLeg = new ModelRenderer(this);
        rightLeg.setPos(-1.9F, 12.0F, 0.0F);
        rightLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        leftLeg = new ModelRenderer(this);
        leftLeg.setPos(1.9F, 12.0F, 0.0F);
        leftLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);
    }

    @Override
    protected Iterable<ModelRenderer> headParts() {
        return Iterables.concat(super.headParts(), ImmutableList.of(this.bipedHead2));
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.bipedLeftArm2, this.bipedLeftArm3, this.bipedRightArm2, this.bipedRightArm3));
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                          float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // update head angles
        bipedHead2.xRot = head.xRot;
        bipedHead2.yRot = head.yRot * -1.0F + 3.14F;
        bipedHead2.zRot = head.zRot + 0.1309F;
        // update arm angles
        // right arms
        float x = rightArm.xRot * 0.85F;
        float y = rightArm.yRot * 0.85F;
        float z = rightArm.zRot * 0.85F;
        // right arm 2
        bipedRightArm2.xRot = -x + 1.1345F;
        bipedRightArm2.yRot = -y + -1.5272F;
        bipedRightArm2.zRot = -z + -0.48F;
        // right arm 3
        bipedRightArm3.xRot = x + 1.0908F;
        bipedRightArm3.yRot = y + 0.5672F;
        bipedRightArm3.zRot = z + 0.9163F;
        // right arm 1
        rightArm.setPos(-5.0F, 2.0F, 0.0F);
        rightArm.xRot += -1.1345F;
        rightArm.yRot += 0.5236F;
        rightArm.zRot += -0.1745F;
        // left arms
        x = leftArm.xRot * 0.85F;
        y = leftArm.yRot * 0.85F;
        z = leftArm.zRot * 0.85F;
        // left arm 2
        bipedLeftArm2.xRot = -x + 0.7854F;
        bipedLeftArm2.yRot = -y + 0.0873F;
        bipedLeftArm2.zRot = -z + -0.0873F;
        // left arm 3
        bipedLeftArm3.xRot = x + 1.0908F;
        bipedLeftArm3.yRot = y + 1.3963F;
        bipedLeftArm3.zRot = z + 0.2182F;
        // left arm 1
        leftArm.setPos(5.0F, 3.0F, 0.0F);
        leftArm.xRot += -0.6109F;
        leftArm.yRot += -0.3491F;
        leftArm.zRot += -0.1745F;
    }

    @Override
    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
    }


}
