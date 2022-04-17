package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.EffectProjectileModel;
import greekfantasy.entity.misc.SwineSpellEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class SwineSpellRenderer<T extends SwineSpellEntity> extends EntityRenderer<T> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/swine_spell.png");
    private static final float SCALE = 0.8F;

    protected EffectProjectileModel<T> entityModel;

    public SwineSpellRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        entityModel = new EffectProjectileModel<T>(0.0F);
    }

    @Override
    public void render(final T entityIn, final float rotationYawIn, final float ageInTicks, final MatrixStack matrixStackIn,
                       final IRenderTypeBuffer bufferIn, final int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.scale(SCALE, -SCALE, SCALE);
        final IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entityIn)));
        super.render(entityIn, rotationYawIn, ageInTicks, matrixStackIn, bufferIn, packedLightIn);
        entityModel.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }

}
