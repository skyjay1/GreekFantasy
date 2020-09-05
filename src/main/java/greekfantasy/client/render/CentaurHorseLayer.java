package greekfantasy.client.render;

import java.util.EnumMap;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.client.model.CentaurModel;
import greekfantasy.entity.CentaurEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.util.ResourceLocation;

public class CentaurHorseLayer<T extends CentaurEntity> extends LayerRenderer<T, CentaurModel<T>> {
  
  public static final EnumMap<CoatColors, ResourceLocation> TEXTURE_MAP = new EnumMap<>(CoatColors.class);
  static {
    TEXTURE_MAP.put(CoatColors.BLACK, new ResourceLocation("minecraft", "textures/entity/horse/horse_black.png"));
    TEXTURE_MAP.put(CoatColors.BROWN, new ResourceLocation("minecraft", "textures/entity/horse/horse_brown.png"));
    TEXTURE_MAP.put(CoatColors.CHESTNUT, new ResourceLocation("minecraft", "textures/entity/horse/horse_chestnut.png"));
    TEXTURE_MAP.put(CoatColors.CREAMY, new ResourceLocation("minecraft", "textures/entity/horse/horse_creamy.png"));
    TEXTURE_MAP.put(CoatColors.DARKBROWN, new ResourceLocation("minecraft", "textures/entity/horse/horse_darkbrown.png"));
    TEXTURE_MAP.put(CoatColors.GRAY, new ResourceLocation("minecraft", "textures/entity/horse/horse_gray.png"));
    TEXTURE_MAP.put(CoatColors.WHITE, new ResourceLocation("minecraft", "textures/entity/horse/horse_white.png"));
  }
  
  public CentaurHorseLayer(IEntityRenderer<T, CentaurModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entity.isInvisible()) {
      // get packed light and a vertex builder bound to the correct texture
      int packedOverlay = LivingRenderer.getPackedOverlay(entity, 0.0F);
      final ResourceLocation texture = TEXTURE_MAP.get(entity.getCoatColor());
      IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(texture));
            
      // render horse body      
      this.getEntityModel().renderHorseBody(entity, matrixStackIn, vertexBuilder, packedLightIn, packedOverlay, limbSwing, limbSwingAmount);
    }
  }
}