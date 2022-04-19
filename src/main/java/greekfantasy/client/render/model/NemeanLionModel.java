package greekfantasy.client.render.model;

import greekfantasy.entity.NemeanLionEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class NemeanLionModel<T extends NemeanLionEntity> extends BigCatModel<T> {

    protected final ModelRenderer mouth;

    public NemeanLionModel() {
        super(64, 64);
        body.texOffs(0, 34).addBox(-5.0F, -11.0F, -4.0F, 10.0F, 4.0F, 10.0F, 0.0F, false);
        body.texOffs(20, 36).addBox(5.0F, -10.0F, -3.0F, 1.0F, 2.0F, 8.0F, 0.0F, false);
        body.texOffs(2, 36).addBox(-6.0F, -10.0F, -3.0F, 1.0F, 2.0F, 8.0F, 0.0F, false);
        body.texOffs(9, 43).addBox(-4.0F, -10.0F, 6.0F, 8.0F, 2.0F, 1.0F, 0.0F, false);
        body.texOffs(13, 43).addBox(-4.0F, -10.0F, -5.0F, 8.0F, 2.0F, 1.0F, 0.0F, false);
        body.texOffs(17, 43).addBox(-3.0F, -7.0F, 4.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

        mouth = new ModelRenderer(this);
        mouth.setPos(0.0F, 3.0F, -3.0F);
        mouth.xRot = 0.5236F;
        mouth.texOffs(15, 14).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 1.0F, 2.0F, 0.0F, false);
        head.addChild(mouth);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        final float idleSwingCos = MathHelper.cos((entity.tickCount + partialTick) * 0.22F);
        mouth.xRot = (0.5236F + 0.06F * idleSwingCos);
    }

    @Override
    protected ModelRenderer makeHeadModel() {
        ModelRenderer head = new ModelRenderer(this);
        head.setPos(0.0F, 10.0F, -10.0F);
        head.texOffs(2, 2).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 4.0F, 0.0F, false);
        head.texOffs(0, 13).addBox(-2.5F, 0.0F, -5.0F, 5.0F, 3.0F, 2.0F, 0.0F, false);
        head.texOffs(15, 17).addBox(-2.0F, 2.5F, -4.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        head.texOffs(21, 1).addBox(-3.5F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        head.texOffs(21, 1).addBox(1.5F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, true);
        return head;
    }

    @Override
    protected boolean isSitting(T entity) {
        return entity.isSitting();
    }
}
