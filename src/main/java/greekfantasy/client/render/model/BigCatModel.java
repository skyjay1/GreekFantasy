package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public abstract class BigCatModel<T extends LivingEntity> extends QuadrupedModel<T> {

    protected ModelRenderer tail;
    protected ModelRenderer tail2;

    private final Vector3f headPoints;

    public BigCatModel(int tWidth, int tHeight) {
        super(0, 0.0F, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
        texWidth = tWidth;
        texHeight = tHeight;

        body = new ModelRenderer(this);
        body.setPos(0.0F, 12.0F, 0.0F);
        body.xRot = 1.5708F;
        body.texOffs(29, 0).addBox(-4.0F, -9.0F, -3.0F, 8.0F, 11.0F, 7.0F, 0.0F, false);
        body.texOffs(35, 19).addBox(-3.0F, 2.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F, false);

        tail = new ModelRenderer(this);
        tail.setPos(0.0F, 10.0F, 10.0F);
        tail.xRot = 0.5236F;
        tail.texOffs(42, 35).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

        tail2 = new ModelRenderer(this);
        tail2.setPos(0.0F, 5.0F, -1.0F);
        tail.addChild(tail2);
        tail2.xRot = 0.5236F;
        tail2.texOffs(47, 35).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        tail2.texOffs(42, 42).addBox(-1.0F, 3.0F, -0.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);

        leg2 = new ModelRenderer(this);
        leg2.setPos(-3.0F, 14.0F, -5.0F);
        leg2.texOffs(0, 19).addBox(-1.5F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, 0.0F, false);
        leg2.texOffs(18, 19).addBox(-1.0F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);
        leg2.addChild(makeClawModel(-1.75F, 1.0F));

        leg0 = new ModelRenderer(this);
        leg0.setPos(-3.0F, 14.0F, 7.0F);
        leg0.texOffs(0, 19).addBox(-0.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);
        leg0.texOffs(18, 19).addBox(-0.5F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);
        leg0.addChild(makeClawModel(-1.25F, 1.0F));

        leg3 = new ModelRenderer(this);
        leg3.setPos(3.0F, 14.0F, -5.0F);
        leg3.texOffs(0, 19).addBox(-2.5F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, 0.0F, false);
        leg3.texOffs(18, 19).addBox(-2.0F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);
        leg3.addChild(makeClawModel(-1.75F, 0.0F));

        leg1 = new ModelRenderer(this);
        leg1.setPos(3.0F, 14.0F, 7.0F);
        leg1.texOffs(18, 19).addBox(-2.5F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);
        leg1.texOffs(0, 19).addBox(-2.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);
        leg1.addChild(makeClawModel(-1.25F, -1.0F));

        head = makeHeadModel();
        headPoints = new Vector3f(head.x, head.y, head.z);
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(tail));
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (isSitting(entity)) {
            // reset rotation points
            body.setPos(0.0F, 18.0F, 0.0F);
            headParts().forEach(m -> m.setPos(headPoints.x(), headPoints.y() + 2.0F, headPoints.z() + 2.0F));
            leg0.setPos(-3.0F, 22.0F, 8.0F);
            leg1.setPos(3.0F, 22.0F, 8.0F);
            tail.setPos(0.0F, 22.0F, 8.0F);
            // reset rotation angles
            body.xRot = 1.0472F;
            leg0.xRot = leg1.xRot = -1.4708F;
            leg2.xRot = leg3.xRot = 0F;
            leg0.yRot = 0.2F;
            leg1.yRot = -0.2F;
        } else {
            // reset rotation points
            body.setPos(0.0F, 12.0F, 0.0F);
            headParts().forEach(m -> m.setPos(headPoints.x(), headPoints.y(), headPoints.z()));
            leg0.setPos(-3.0F, 14.0F, 7.0F);
            leg1.setPos(3.0F, 14.0F, 7.0F);
            tail.setPos(0.0F, 10.0F, 10.0F);
            // reset rotation angles
            body.xRot = 1.5708F;
            leg0.yRot = leg1.yRot = 0.0F;
        }
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        float limbSwingCos = MathHelper.cos(limbSwing) * limbSwingAmount;
        float idleSwing = 0.1F * MathHelper.cos((entity.tickCount + partialTick) * 0.08F);
        float tailSwing = 0.42F * limbSwingCos;
        tail.xRot = 0.6854F + tailSwing;
        tail2.xRot = 0.3491F + tailSwing * 0.6F;
        tail.zRot = idleSwing;
        tail2.zRot = idleSwing * 0.85F;
        body.zRot = limbSwingCos * 0.12F;
        // reset angles when sitting
        if (isSitting(entity)) {
            tail.xRot += 0.7F;
        }
    }

    /**
     * Create a model for the claws
     *
     * @param startX should be -1.75F for front, and -1.25F for back
     * @param rotX   changes for each claw
     * @return the claws model that was created
     */
    protected ModelRenderer makeClawModel(final float startX, final float rotX) {
        final ModelRenderer claws = new ModelRenderer(this);
        claws.setPos(rotX, 9.0F, -1.5F);
        claws.xRot = -0.7854F;
        claws.texOffs(0, 29).addBox(startX, 0.0F, 0.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
        return claws;
    }

    protected abstract ModelRenderer makeHeadModel();

    protected abstract boolean isSitting(T entity);
}
