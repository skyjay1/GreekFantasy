package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.entity.HarpyEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class HarpyModel<T extends HarpyEntity> extends BipedModel<T> {

    private final ModelRenderer chest;
    private final ModelRenderer tail;
    private final ModelRenderer leftWing1;
    private final ModelRenderer leftWing2;
    private final ModelRenderer leftWing3;
    private final ModelRenderer rightWing1;
    private final ModelRenderer rightWing2;
    private final ModelRenderer rightWing3;

    public HarpyModel(final float modelSize) {
        super(modelSize, 0.0F, 64, 64);

        head.texOffs(24, 0).addBox(-4.0F, 0.0F, 4.0F, 8.0F, 5.0F, 0.0F, modelSize);

        chest = new ModelRenderer(this);
        chest.setPos(0.0F, 1.0F, -2.0F);
        chest.xRot = -0.2182F;
        chest.texOffs(19, 20).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);

        tail = new ModelRenderer(this);
        tail.setPos(0.0F, 12.0F, 2.0F);
        tail.xRot = 0.3491F;
        tail.texOffs(48, 57).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 7.0F, 0.0F, modelSize);

        this.leftLeg = new ModelRenderer(this);
        this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
        this.leftLeg.texOffs(0, 33).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 11.0F, 3.0F, modelSize);
        this.leftLeg.mirror = true;

        this.rightLeg = new ModelRenderer(this);
        this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
        this.rightLeg.texOffs(0, 17).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 11.0F, 3.0F, modelSize);

        this.leftLeg.addChild(makeFoot(modelSize, true));
        this.rightLeg.addChild(makeFoot(modelSize, false));

        leftWing1 = new ModelRenderer(this);
        leftWing1.setPos(4.0F, 1.0F, 0.0F);
        leftWing1.texOffs(0, 48).addBox(0.0F, 0.0F, 0.0F, 6.0F, 10.0F, 1.0F, 0.0F, true);
        leftWing1.mirror = true;

        leftWing2 = new ModelRenderer(this);
        leftWing2.setPos(6.0F, 0.0F, 1.0F);
        leftWing1.addChild(leftWing2);
        leftWing2.texOffs(15, 48).addBox(0.0F, 0.0F, -1.05F, 8.0F, 14.0F, 1.0F, 0.0F, true);
        leftWing2.mirror = true;

        leftWing3 = new ModelRenderer(this);
        leftWing3.setPos(8.0F, 0.0F, 0.0F);
        leftWing2.addChild(leftWing3);
        leftWing3.texOffs(34, 48).addBox(0.0F, 0.0F, -1.1F, 6.0F, 10.0F, 1.0F, 0.0F, true);
        leftWing3.mirror = true;

        rightWing1 = new ModelRenderer(this);
        rightWing1.setPos(-4.0F, 1.0F, 0.0F);
        rightWing1.texOffs(0, 48).addBox(-6.0F, 0.0F, 0.0F, 6.0F, 10.0F, 1.0F, modelSize);

        rightWing2 = new ModelRenderer(this);
        rightWing2.setPos(-6.0F, 0.0F, 1.0F);
        rightWing1.addChild(rightWing2);
        rightWing2.texOffs(15, 48).addBox(-8.0F, 0.0F, -1.05F, 8.0F, 14.0F, 1.0F, modelSize);

        rightWing3 = new ModelRenderer(this);
        rightWing3.setPos(-8.0F, 0.0F, 0.0F);
        rightWing2.addChild(rightWing3);
        rightWing3.texOffs(34, 48).addBox(-6.0F, 0.0F, -1.1F, 6.0F, 10.0F, 1.0F, modelSize);

        // hide biped arms
        this.rightArm.visible = false;
        this.leftArm.visible = false;
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return ImmutableList.of(this.body, this.chest, this.tail, this.rightLeg, this.leftLeg, this.rightWing1, this.leftWing1, this.hat);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        final float flyingTime = entity.flyingTime;
        final float flyingTimeLeft = 1.0F - flyingTime;
        // animate legs (only while flying)
        this.leftLeg.xRot *= (flyingTimeLeft * 0.6F);
        this.leftLeg.xRot += (-0.35F * flyingTime);
        this.rightLeg.xRot *= (flyingTimeLeft * 0.6F);
        this.rightLeg.xRot += (-0.35F * flyingTime);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        float ticks = entity.getId() * 2 + entity.tickCount + partialTick;
        final float flyingTime = entity.flyingTime;
        final float flyingTimeLeft = 1.0F - flyingTime;
        final float downSwing = 0.5F;
        final float wingAngle = 0.5F;
        final float wingSpeed = 0.7F;
        final float cosTicks = flyingTime > 0.0F ? MathHelper.cos(ticks * wingSpeed) : 0.0F;
        final float sinTicks = flyingTime > 0.0F ? MathHelper.cos(ticks * wingSpeed + (float) Math.PI) : 0.0F;
        final float idleSwing = 0.035F * MathHelper.cos(ticks * 0.08F);

        // animate wings (combines flying and landing animations)
        this.leftWing1.xRot = 1.0472F - 0.7854F * flyingTime;
        this.leftWing1.yRot = 0.0F + ((cosTicks + downSwing) * wingAngle * 0.75F) * flyingTime;
        this.leftWing1.zRot = 0.9908F - 0.8908F * flyingTime + idleSwing;

        this.leftWing2.yRot = 0.5236F * flyingTimeLeft + ((cosTicks + downSwing) * wingAngle) * flyingTime;
        this.leftWing3.yRot = 0.1745F * flyingTimeLeft + ((cosTicks + downSwing) * wingAngle) * flyingTime;

        this.rightWing1.xRot = this.leftWing1.xRot;
        this.rightWing1.yRot = 0.0F + ((sinTicks - downSwing) * 0.32F) * flyingTime;
        this.rightWing1.zRot = -0.9908F + 0.8908F * flyingTime - idleSwing;

        this.rightWing2.yRot = -0.5236F * flyingTimeLeft + ((sinTicks - downSwing) * wingAngle) * flyingTime;
        this.rightWing3.yRot = -0.1745F * flyingTimeLeft + ((sinTicks - downSwing) * wingAngle) * flyingTime;
    }

    private ModelRenderer makeFoot(final float modelSize, final boolean isLeft) {
        final float offsetX = isLeft ? 1.0F : 0.0F;
        final float rotationToe = 0.3491F;

        final ModelRenderer foot = new ModelRenderer(this);
        foot.setPos(0.0F, 0.0F, 0.0F);

        final ModelRenderer frontToe1 = new ModelRenderer(this);
        frontToe1.setPos(offsetX - 1.0F, 10.0F, -2.0F);
        frontToe1.xRot = rotationToe;
        frontToe1.yRot = rotationToe;
        frontToe1.texOffs(13, 38).addBox(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 4.0F, modelSize);
        foot.addChild(frontToe1);

        final ModelRenderer frontToe2 = new ModelRenderer(this);
        frontToe2.setPos(offsetX, 10.0F, -2.0F);
        frontToe2.xRot = rotationToe;
        frontToe2.yRot = -rotationToe;
        frontToe2.texOffs(13, 38).addBox(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 4.0F, modelSize);
        foot.addChild(frontToe2);

        final ModelRenderer backToe = new ModelRenderer(this);
        backToe.setPos(offsetX, 10.0F, 1.0F);
        foot.addChild(backToe);
        backToe.xRot = -0.5236F;
        backToe.texOffs(13, 33).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, modelSize);

        return foot;
    }
}