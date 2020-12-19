package greekfantasy.client.render;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.GiantBoarModel;
import greekfantasy.entity.GiantBoarEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class GiantBoarRenderer<T extends GiantBoarEntity> extends MobRenderer<T, GiantBoarModel<T>> {

  private static final ResourceLocation HOGLIN_TEXTURE = new ResourceLocation("textures/entity/hoglin/hoglin.png");
  private static final ResourceLocation GIANT_BOAR_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/giant_boar.png");
  public static final float SCALE = 1.9F;
  
  protected boolean isAlphaLayer;
  
  public GiantBoarRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new GiantBoarModel<>(), 1.0F);
  }
  
  @Override
  public void render(final T entity, final float entityYaw, final float partialTicks, final MatrixStack matrixStackIn,
      final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    if(!entity.isInvisible()) {
      matrixStackIn.push();
      // scale the models
      final float spawnPercent = entity.getSpawnPercent(partialTicks % 1.0F);
      final float scale = 1.0F + (SCALE - 1.0F) * spawnPercent;
      matrixStackIn.scale(scale, scale, scale);
      
      // render the base model (erymanthian texture)
      this.getEntityModel().setColorAlpha(1.0F);
      super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
       
      // prepare to render transparent layer
      if(spawnPercent < 0.99F) {
        isAlphaLayer = true;
        matrixStackIn.push();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        // render transparent layer
        this.entityModel.setColorAlpha(1.0F - spawnPercent);
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
        matrixStackIn.pop();
        isAlphaLayer = false;
      }
      matrixStackIn.pop();
    }
  }
  
  @Override
  protected boolean func_230495_a_(T entity) {
     return entity.func_234364_eK_();
  }
  
  @Override
  protected void preRenderCallback(final T entity, MatrixStack matrix, float ageInTicks) {
    
  }

  @Override
  public ResourceLocation getEntityTexture(T entity) {
     return GIANT_BOAR_TEXTURE;
  }
  
  @Override
  @Nullable
  protected RenderType func_230496_a_(final T entity, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
    return isAlphaLayer ? RenderType.getEntityTranslucent(HOGLIN_TEXTURE, isGlowing) : super.func_230496_a_(entity, isVisible, isVisibleToPlayer, isGlowing);
  }
}
