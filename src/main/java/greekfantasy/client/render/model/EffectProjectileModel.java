package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.projectile.ProjectileEntity;

public class EffectProjectileModel<T extends ProjectileEntity> extends EntityModel<T> {

    private final ModelRenderer cross;

    public EffectProjectileModel(final float modelSize) {
        texWidth = 16;
        texHeight = 16;

        cross = new ModelRenderer(this);
        cross.setPos(0.0F, 0.0F, 0.0F);
        cross.texOffs(0, 0).addBox(0.0F, -8.0F, -4.0F, 0.0F, 8.0F, 8.0F, modelSize);
        cross.texOffs(0, 8).addBox(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 0.0F, modelSize);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // do nothing
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
                               float green, float blue, float alpha) {
        cross.render(matrixStackIn, bufferIn, 15728880, packedOverlayIn);
    }

}
