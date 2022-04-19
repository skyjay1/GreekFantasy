package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.math.MathHelper;

public class PegasusModel<T extends AbstractHorseEntity> extends HorseModel<T> {

    private final ModelRenderer leftWing;
    private final ModelRenderer rightWing;

    public PegasusModel(float modelSize) {
        super(modelSize);

        leftWing = new ModelRenderer(this);
        leftWing.setPos(5.0F, -8.0F, -7.0F);
        leftWing.yRot = -1.5708F;
        leftWing.texOffs(0, 0).addBox(-6.0F, 0.0F, 0.0F, 11.0F, 20.0F, 1.0F, modelSize, true);

        rightWing = new ModelRenderer(this);
        rightWing.setPos(-5.0F, -8.0F, -7.0F);
        rightWing.yRot = 1.5708F;
        rightWing.texOffs(0, 0).addBox(-6.0F, 0.0F, 0.0F, 11.0F, 20.0F, 1.0F, modelSize, false);
    }

    @Override
    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
        // calculate wing rotations
        final float wingSpan = 0.6F;
        final float wingSpeed = 0.08F + (entityIn.isVehicle() ? 0.32F : 0);
        final float wingAngle = 1.5708F + MathHelper.cos((entityIn.tickCount + entityIn.getId() * 3 + partialTick) * wingSpeed) * wingSpan;
        // update rotations
        leftWing.xRot = body.xRot;
        rightWing.xRot = body.xRot;
        leftWing.zRot = -wingAngle + body.zRot;
        rightWing.zRot = wingAngle + body.zRot;
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                          float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // copy rotation point from body
        this.leftWing.setPos(5.0F + this.body.x, -8.0F + this.body.y, -7.0F + this.body.z);
        this.rightWing.setPos(-5.0F + this.body.x, -8.0F + this.body.y, -7.0F + this.body.z);
    }

    public void renderWings(T entity, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn,
                            int packedOverlayIn, float limbSwing, float limbSwingAmount, float partialTick) {
        // actually render the wings
        this.leftWing.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.rightWing.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }
}
