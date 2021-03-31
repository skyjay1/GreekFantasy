package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.CentaurModel;
import greekfantasy.entity.CentaurEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

public class CentaurQuiverLayer<T extends CentaurEntity> extends LayerRenderer<T, CentaurModel<T>> {
  
  protected static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/quiver.png");

  public CentaurQuiverLayer(IEntityRenderer<T, CentaurModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    final ItemStack bow = entity.getHeldItem(ProjectileHelper.getHandWith(entity, Items.BOW));
    if (!entity.isInvisible() && bow.getItem() == Items.BOW) {
      // get packed light and a vertex builder bound to the correct texture
      int packedOverlay = LivingRenderer.getPackedOverlay(entity, 0.0F);
      IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(TEXTURE));
            
      // render quiver
      this.getEntityModel().renderQuiver(entity, matrixStackIn, vertexBuilder, packedLightIn, packedOverlay, limbSwing, limbSwingAmount);
    }
  }
}